using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using SourceAFIS.Simple;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media.Imaging;

namespace Bionic
{
    class Program
    {
        private static string picturePath = "D:\\Card\\Data";
        private static string templatePath = "D:\\Card\\Data\\template";
        private static AfisEngine afisEngine = new AfisEngine();

        private static string Q_CAPTURE_REQUEST = "Q_CAPTURE_REQUEST";
        private static string Q_CAPTURE_RESPONSE = "Q_CAPTURE_RESPONSE";

        private static string Q_PERFORM_VERIFY_REQUEST = "Q_PERFORM_VERIFY_REQUEST";
        private static string Q_PERFORM_VERIFY_RESPONSE = "Q_PERFORM_VERIFY_RESPONSE";

        static void Main(string[] args)
        {
            var factory = new ConnectionFactory() {
                HostName = "localhost",
                UserName = "guest",
                Password = "guest",
                Port = 5672
            };

            var connection = factory.CreateConnection();

            var channel = connection.CreateModel();
                
            Console.WriteLine("Connected successfully to RabbitMQ!");

            channel.QueueDeclare(queue: Q_CAPTURE_REQUEST, durable: false, exclusive: false, autoDelete: false, arguments: null);

            var consumer = new EventingBasicConsumer(channel);

            consumer.Received += (model, ea) =>
            {
                var body = ea.Body;
                var message = Encoding.UTF8.GetString(body);
                string response = "SUCCESS";

                Console.WriteLine(" [Q_CAPTURE_REQUEST] Received {0}", message);

                try
                {
                    var accessor = new DeviceAccessor();
                    var device = accessor.AccessFingerprintDevice();

                    device.SwitchLedState(false, false);

                    device.FingerDetected += (sender, eventArgs) =>
                    {
                        Console.WriteLine("Finger Detected!");

                        device.SwitchLedState(true, false);

                    // Save fingerprint to temporary folder
                    var fingerprint = device.ReadFingerprint();
                        var tempFile = picturePath + "\\" + message + ".bmp";
                        fingerprint.Save(tempFile);

                        Console.WriteLine("Saved to " + tempFile);

                        Fingerprint fp = new Fingerprint();
                        fp.AsBitmapSource = new BitmapImage(new Uri(tempFile, UriKind.RelativeOrAbsolute));
                        Person ps = new Person();
                        ps.Fingerprints.Add(fp);
                        afisEngine.Extract(ps);
                        File.WriteAllBytes(templatePath + "\\" + message + ".tmpl", fp.AsIsoTemplate);

                        Console.WriteLine("Template saved successfully !");

                    // TODO Notify to RabbitMQ
                    sendToQueue(channel, Q_CAPTURE_RESPONSE, response);

                        device.SwitchLedState(false, false);
                        device.Dispose();
                    };

                    device.FingerReleased += (sender, eventArgs) =>
                    {
                        Console.WriteLine("Finger Released!");
                        device.SwitchLedState(false, true);
                    };

                    Console.WriteLine("Device Opened for capture");

                    device.StartFingerDetection();
                    device.SwitchLedState(false, true);
                }
                catch (Exception ex)
                {
                    response = "ERROR";
                    Console.WriteLine("Fingerprint Extraction: " + ex.Message);
                    sendToQueue(channel, Q_CAPTURE_RESPONSE, response);
                }
            };

            channel.BasicConsume(queue: Q_CAPTURE_REQUEST, autoAck: true, consumer: consumer);
               
            channel.QueueDeclare(queue: Q_PERFORM_VERIFY_REQUEST, durable: false, exclusive: false, autoDelete: false, arguments: null);

            var consumer1 = new EventingBasicConsumer(channel);

            consumer1.Received += (model, ea) =>
            {
                byte[] body = ea.Body;
                string response = "SUCCESS";

                // Console.WriteLine(" [Q_PERFORM_VERIFY_REQUEST] Received {0}", Encoding.UTF8.GetString(body));

                try
                {
                    var accessor = new DeviceAccessor();
                    var device = accessor.AccessFingerprintDevice();

                    device.SwitchLedState(false, false);

                    device.FingerDetected += (sender, eventArgs) =>
                    {
                        Console.WriteLine("Finger Detected!");

                        device.SwitchLedState(true, false);

                        // Save fingerprint to temporary folder
                        var fingerprint = device.ReadFingerprint();
                        var tempFile = Path.ChangeExtension(Path.GetTempFileName(), "bmp");
                        fingerprint.Save(tempFile);

                        // Console.WriteLine("Saved to " + tempFile);

                        Fingerprint strfp = new Fingerprint();
                        strfp.AsIsoTemplate = body;
                        Person storedPerson = new Person(strfp);

                        try
                        {
                            Fingerprint fp = new Fingerprint();
                            fp.AsBitmapSource = new BitmapImage(new Uri(tempFile, UriKind.RelativeOrAbsolute));
                            Person scannedPerson = new Person();
                            scannedPerson.Fingerprints.Add(fp);
                            afisEngine.Extract(scannedPerson);

                            float verify = afisEngine.Verify(storedPerson, scannedPerson);
                            if (verify != 0)
                            {
                                Console.WriteLine("Success : " + verify);
                            }
                            else
                            {
                                response = "FAILED";
                                Console.WriteLine("Failed : " + verify);
                            }

                            // Send to RabbitMQ
                            sendToQueue(channel, Q_PERFORM_VERIFY_RESPONSE, response);

                            device.SwitchLedState(false, false);
                            device.Dispose();
                        }
                        catch (Exception ex)
                        {
                            response = "ERROR";
                            Console.WriteLine("Fingerprint Extraction: " + ex.Message);
                            sendToQueue(channel, Q_PERFORM_VERIFY_RESPONSE, response);
                        }
                    };

                    Console.WriteLine("Device Opened for Matching");

                    device.StartFingerDetection();
                    device.SwitchLedState(false, true);
                }
                catch (Exception ex)
                {
                    response = "ERROR";
                    Console.WriteLine("Fingerprint Extraction: " + ex.Message);
                    sendToQueue(channel, Q_PERFORM_VERIFY_RESPONSE, response);
                }
            };

            channel.BasicConsume(queue: Q_PERFORM_VERIFY_REQUEST, autoAck: true, consumer: consumer1);
            
            Console.WriteLine("Press any key to exit!");
            Console.ReadLine();
        }

        private static void sendToQueue(IModel channel, string queueName, string message)
        {
            channel.QueueDeclare(queue: queueName, durable: false, exclusive: false, autoDelete: false, arguments: null);
            
            var body = Encoding.UTF8.GetBytes(message);

            channel.BasicPublish(exchange: "", routingKey: queueName, basicProperties: null, body: body);
        }
    }
}
