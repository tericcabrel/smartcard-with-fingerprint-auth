package com.tericcabrel;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.tericcabrel.fingerprint.FingerPrint;
import com.tericcabrel.fingerprint.FingerprintScanner;
import com.tericcabrel.services.OsirisCardService;
import com.tericcabrel.utils.CardHelper;
import com.tericcabrel.utils.Helpers;
import com.tericcabrel.utils.Messaging;

import javax.smartcardio.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

public class Main {
    private static Channel channel;

    public static void main(String[] args) {
        // Connection to RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        try {
            Connection connection = factory.newConnection();
            System.out.println(" Connected successfully to RabbitMQ Server");

            channel = connection.createChannel();
        }catch (TimeoutException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                try {
                    Card newCard = CardHelper.getCard();

                    if (OsirisCardService.getCard() == null && newCard != null) {
                        System.out.println("Card inserted");

                        OsirisCardService.setCard(newCard);

                        String message = OsirisCardService.selectApplet();

                        Messaging.sendToQueue(channel, Messaging.Q_APPLET_SELECTED_RESPONSE, message);
                    } else if (OsirisCardService.getCard() != null && newCard == null) {
                        System.out.println("Card removed");

                        OsirisCardService.setCard(null);

                        Messaging.sendToQueue(channel, Messaging.Q_CARD_REMOVED_RESPONSE, OsirisCardService.SW_CARD_REMOVED);
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                    // TODO Stop the timer and notify that the service is down
                }
            }
        },0,1000);

        try {
            channel.queueDeclare(Messaging.Q_AUTHENTICATE_REQUEST, false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");
                String response = OsirisCardService.authenticate(message);
                Messaging.sendToQueue(channel, Messaging.Q_AUTHENTICATE_RESPONSE, response);
            };
            channel.basicConsume(Messaging.Q_AUTHENTICATE_REQUEST, true, deliverCallback, consumerTag -> { });

            channel.queueDeclare(Messaging.Q_GET_DATA_REQUEST, false, false, false, null);
            DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                String response = OsirisCardService.getData();
                Messaging.sendToQueue(channel, Messaging.Q_GET_DATA_RESPONSE, response);
            };
            channel.basicConsume(Messaging.Q_GET_DATA_REQUEST, true, deliverCallback1, consumerTag -> { });

            channel.queueDeclare(Messaging.Q_SET_DATA_REQUEST, false, false, false, null);
            DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");
                String response = OsirisCardService.setData(message);
                Messaging.sendToQueue(channel, Messaging.Q_SET_DATA_RESPONSE, response);
            };
            channel.basicConsume(Messaging.Q_SET_DATA_REQUEST, true, deliverCallback2, consumerTag -> { });

            channel.queueDeclare(Messaging.Q_SET_NAME_REQUEST, false, false, false, null);
            DeliverCallback deliverCallback3 = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");
                String response = OsirisCardService.setName(message);
                Messaging.sendToQueue(channel, Messaging.Q_SET_NAME_RESPONSE, response);
            };
            channel.basicConsume(Messaging.Q_SET_NAME_REQUEST, true, deliverCallback3, consumerTag -> { });

            channel.queueDeclare(Messaging.Q_SET_BIRTH_DATE_REQUEST, false, false, false, null);
            DeliverCallback deliverCallback4 = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");
                String response = OsirisCardService.setBirthDate(message);
                Messaging.sendToQueue(channel, Messaging.Q_SET_BIRTH_DATE_RESPONSE, response);
            };
            channel.basicConsume(Messaging.Q_SET_BIRTH_DATE_REQUEST, true, deliverCallback4, consumerTag -> { });

            channel.queueDeclare(Messaging.Q_RESET_REQUEST, false, false, false, null);
            DeliverCallback deliverCallback5 = (consumerTag, delivery) -> {
                String response = OsirisCardService.resetData();
                Messaging.sendToQueue(channel, Messaging.Q_RESET_RESPONSE, response);
            };
            channel.basicConsume(Messaging.Q_RESET_REQUEST, true, deliverCallback5, consumerTag -> { });

            channel.queueDeclare(Messaging.Q_UNBLOCK_REQUEST, false, false, false, null);
            DeliverCallback deliverCallback6 = (consumerTag, delivery) -> {
                String response = OsirisCardService.unblock();
                Messaging.sendToQueue(channel, Messaging.Q_UNBLOCK_RESPONSE, response);
            };
            channel.basicConsume(Messaging.Q_UNBLOCK_REQUEST, true, deliverCallback6, consumerTag -> { });

            channel.queueDeclare(Messaging.Q_DISCONNECT_REQUEST, false, false, false, null);
            DeliverCallback deliverCallback7 = (consumerTag, delivery) -> {
                OsirisCardService.disconnect();
                Messaging.sendToQueue(channel, Messaging.Q_DISCONNECT_RESPONSE, "OK");
            };
            channel.basicConsume(Messaging.Q_DISCONNECT_REQUEST, true, deliverCallback7, consumerTag -> { });

            channel.queueDeclare(Messaging.Q_ENROLL_REQUEST, false, false, false, null);
            DeliverCallback deliverCallback8 = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8); // Received the uid
                System.out.println(" [x] Received '" + message + "'"); // contains user's uid

                String folderPath = "D:\\Card\\OsirisCore\\data";

                // Get fingerprint
                FingerPrint fp = new FingerPrint(folderPath, message);
                String response = "12000";

                if (fp.isSdkInitialized()) {
                    List<FingerprintScanner> scanners = fp.getScanners();
                    if (scanners.size() > 0) {
                        fp.setParameters(0);

                        fp.captureSingle();

                        if (fp.isFingerCaptured()) {
                            fp.saveImage();

                            boolean b = fp.enroll(true);
                            if (b)  {
                                // Write fingerPrint template in the card
                                OsirisCardService.setFingerprint(fp.getCurrentTemplate());

                                response = "12500";

                                // Upload the fingerprint to the server
                                String res = Helpers.uploadFingerprint(
                                        message,
                                        folderPath + "\\" + message + ".dat",
                                        folderPath + "\\" + message + ".png"
                                );

                                if (!res.equals("RES200")) {
                                    response = "12400";
                                }
                            }
                        } else {
                            response = "12300";
                        }
                    } else {
                        response = "12200";
                    }
                } else {
                    response = "12100";
                }

                Messaging.sendToQueue(channel, Messaging.Q_ENROLL_RESPONSE, response);
            };
            channel.basicConsume(Messaging.Q_ENROLL_REQUEST, true, deliverCallback8, consumerTag -> { });

            channel.queueDeclare(Messaging.Q_VERIFY_USER_REQUEST, false, false, false, null);
            DeliverCallback deliverCallback10 = (consumerTag, delivery) -> {
                String info = OsirisCardService.getData();
                System.out.println(info);

                String[] array = info.split(OsirisCardService.DATA_DELIMITER);

                System.out.println(array.length);
                if (array.length != 4) {
                    Messaging.sendToQueue(channel, Messaging.Q_VERIFY_USER_RESPONSE, info);
                    return;
                }

                String response = info;
                String templatePath = "D:\\Card\\OsirisCore\\data\\" + array[0] + ".dat";
                System.out.println(templatePath);

                byte[] storedFingerprint = OsirisCardService.getFingerpint(Integer.valueOf(array[array.length - 1]));

                FingerPrint fp = new FingerPrint("D:\\Card\\OsirisCore\\data", array[0]);
                if (fp.isSdkInitialized()) {
                    List<FingerprintScanner> scanners = fp.getScanners();
                    if (scanners.size() > 0) {
                        fp.setParameters(0);

                        fp.captureSingle();

                        if (fp.isFingerCaptured()) {
                            boolean b = fp.enroll(false);
                            if (b)  {
                                boolean res = fp.verify(storedFingerprint, fp.getCurrentTemplate());
                                if (!res) {
                                    response = "17900";
                                }
                            }
                        } else {
                            response = "12300";
                        }
                    } else {
                        response = "12200";
                    }
                } else {
                    response = "12100";
                }

                Messaging.sendToQueue(channel, Messaging.Q_VERIFY_USER_RESPONSE, response);
            };
            channel.basicConsume(Messaging.Q_VERIFY_USER_REQUEST, true, deliverCallback10, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}