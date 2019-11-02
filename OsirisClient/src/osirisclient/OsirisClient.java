package osirisclient;

import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;
import com.sun.javacard.apduio.CadTransportException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author ZEGEEK
 */
public class OsirisClient {
    
    /* Constants */
    public static final byte CLA_OSIRIS = (byte) 0x3A;

    public static final byte INS_GET_DATA = 0x00;
    public static final byte INS_SET_DATA = 0x01;
    public static final byte INS_SET_NAME = 0x02;
    public static final byte INS_SET_BIRTHDATE = 0x03;
    public static final byte INS_RESET_DATA = 0x04;
    private final static byte INS_PIN_AUTH = (byte) 0x05;
    private final static byte INS_PIN_UNBLOCK = (byte) 0x06;
    private final static byte INS_SET_FINGERPRINT = (byte) 0x07;
    private final static byte INS_GET_FINGERPRINT = (byte) 0x08;
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws com.sun.javacard.apduio.CadTransportException
     */
    public static void main(String[] args) throws IOException, CadTransportException {
        // Connect to Java Card
        CadT1Client cad;
        Socket sckCarte;
        
        try {
            sckCarte = new Socket("localhost", 9025);
            sckCarte.setTcpNoDelay(true);
            BufferedInputStream input = new BufferedInputStream(sckCarte.getInputStream());
            BufferedOutputStream output = new BufferedOutputStream(sckCarte.getOutputStream());
            cad = new CadT1Client(input, output);
        } catch (IOException e) {
            System.out.println("Error: Can not connect to Java Card");
            return;
        }		
		
        // Turning on the card
        try {
            cad.powerUp();
        } catch (CadTransportException | IOException e) {
            System.out.println("Error sending the powerUp command to the Java Card");
            return;
        }

        // Select the applet
        Apdu apdu = new Apdu();
        apdu.command[Apdu.CLA] = 0x00;
        apdu.command[Apdu.INS] = (byte) 0xA4;
        apdu.command[Apdu.P1] = 0x04;
        apdu.command[Apdu.P2] = 0x00;
        
        byte[] appletAID = { (byte) 0x61, (byte) 0xC1, (byte) 0x9D, (byte) 0x2B, (byte) 0x05, (byte) 0x35 };
        // byte[] appletAID = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00 };
        
        apdu.setDataIn(appletAID);
        cad.exchangeApdu(apdu);
        if (apdu.getStatus() != 0x9000) {
            System.out.println("Error while selecting the applet: " + apdu.getStatus());
            System.exit(1);
        }
			
        // Main menu principal
        boolean end = false;
        while (!end) {
            System.out.println();
            System.out.println("ORISIS CLIENT");
            System.out.println("----------------------------");
            System.out.println();
            System.out.println("1 - AUTHENTICATE");
            System.out.println("2 - GET DATA");
            System.out.println("3 - SET DATA");
            System.out.println("4 - SET NAME");
            System.out.println("5 - SET BIRTH DATE");
            System.out.println("6 - RESET");
            System.out.println("7 - UNBLOCK");
            System.out.println("8 - SET FINGERPRINT");
            System.out.println("9 - GET FINGERPRINT");
            System.out.println("10 - QUIT");
            System.out.println();
            System.out.println("Your choice: ");

            Scanner sc = new Scanner(System.in);
            String choice = sc.nextLine();
            while (!(Integer.valueOf(choice) >= 1 && Integer.valueOf(choice) <= 10)) {
                choice = sc.nextLine();
            }

            apdu = new Apdu();
            apdu.command[Apdu.CLA] = OsirisClient.CLA_OSIRIS;
            apdu.command[Apdu.P1] = 0x00;
            apdu.command[Apdu.P2] = 0x00;

            switch (Integer.valueOf(choice)) {
                case 1:
                    apdu.command[Apdu.INS] = OsirisClient.INS_PIN_AUTH;
                    byte[] pinCode = Utils.numberStringToByteArray("123456");
                    
                    apdu.setDataIn(pinCode);
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case 2:
                    apdu.command[Apdu.INS] = OsirisClient.INS_GET_DATA;
                    cad.exchangeApdu(apdu);

                    if (apdu.getStatus() != 0x9000) {
                        System.out.println("An error occurred with status: " + apdu.getStatus());
                    } else {
                        System.out.print("Data : " + Utils.byteArrayToString(apdu.dataOut));
                    }
                    break;
                case 3:
                    apdu.command[Apdu.INS] = OsirisClient.INS_SET_DATA;
                    byte[] data = "uid|name|birth".getBytes();

                    apdu.setDataIn(data);
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case 4:
                    apdu.command[Apdu.INS] = OsirisClient.INS_SET_NAME;
                    byte[] nameData = "tericcabrel".getBytes();

                    apdu.setDataIn(nameData);
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case 5:
                    apdu.command[Apdu.INS] = OsirisClient.INS_SET_BIRTHDATE;
                    byte[] birthData = "5991-30-14".getBytes();

                    apdu.setDataIn(birthData);
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case 6:
                    apdu.command[Apdu.INS] = OsirisClient.INS_RESET_DATA;
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case 7:
                    apdu.command[Apdu.INS] = OsirisClient.INS_PIN_UNBLOCK;
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    break;
                case 8:
                    byte[] fingerData = fileToByteArray("D:\\Card\\tecoright-two.dat");
                    byte[] Data = new byte[100];

                    // System.out.println("Finger Length: " + fingerData.length);

                    int fingerLength = fingerData.length;
                    double round = Math.floor((double)fingerLength / 100);
                    int lastPart = fingerLength % 100;

                    // System.out.println("Round : " + round);
                    // System.out.println("Last Part : " + lastPart);

                    byte[] dest = new byte[fingerLength];

                    byte[] lengthToByte = Utils.prepareNumberForApdu(fingerLength);
                    
                    apdu.command[Apdu.INS] = OsirisClient.INS_SET_FINGERPRINT;
                    apdu.command[Apdu.P1] = 0x00;
                    apdu.command[Apdu.P2] = 0x00;
                    
                    apdu.setDataIn(lengthToByte);
                    
                    cad.exchangeApdu(apdu);
                    handleResponse(apdu);
                    
                    boolean allPartSend = true;
                    for (int i = 0; i < round; i++) {
                        // Array copy: (src, offset, target, offset, copy size)
                        System.arraycopy(fingerData, (i * 100), Data, 0, 100);
                        // System.out.println("Data Length: " + Data.length);
                        System.arraycopy(Data, 0, dest, (i * 100), 100);
                        
                        apdu.command[Apdu.P1] = (byte) i;
                        apdu.command[Apdu.P2] = 0x64;
                        apdu.setLc(100);
                        
                        apdu.setDataIn(Data);
                        cad.exchangeApdu(apdu);
                        if (apdu.getStatus() != 0x9000) {
                            System.out.println("["+i+"] An error occurred with status: " + apdu.getStatus());
                            allPartSend = false;
                            break;
                        }
                    }
                    
                    if(allPartSend) {
                        System.arraycopy(fingerData,  (int) (round * 100), dest, (int) (round * 100), lastPart);
                        byte[] finalPart = new byte[lastPart];
                        System.arraycopy(fingerData, (int) (round * 100), finalPart, 0, lastPart);
                        
                        apdu.command[Apdu.P1] = (byte) round;
                        apdu.command[Apdu.P2] = (byte) lastPart;
                        apdu.setLc(lastPart);
                        
                        apdu.setDataIn(finalPart);
                        cad.exchangeApdu(apdu);
                        if (apdu.getStatus() != 0x9000) {
                            System.out.println("An error occurred with status: " + apdu.getStatus());
                            break;
                        } else {
                            System.out.println("OK");
                        }
                    } else {
                        System.out.println("Fail to send all data");
                    }
                    
                    byte[] fake = new byte[fingerLength];
                    System.arraycopy(fingerData, 0, fake, 0, fingerLength);

                    // System.out.println("Dest Length : " + dest.length);
                    // System.out.println("Array equals : " + Arrays.equals(fingerData, dest));
                    // System.out.println("Array equals : " + Arrays.equals(fingerData, fake));
                    break;
                case 9:
                    int fpLength = 553;
                    byte[] res = new byte[fpLength];
                    double rnd = Math.floor((double)fpLength / 100);
                    int last = fpLength % 100;
                    
                    boolean allSend = true;
                    
                    for(int i = 0; i < rnd; i++) {
                        apdu.command[Apdu.INS] = OsirisClient.INS_GET_FINGERPRINT;
                        apdu.command[Apdu.P1] = (byte) i;
                        apdu.command[Apdu.P2] = 0x64;
                        
                        cad.exchangeApdu(apdu);

                        if (apdu.getStatus() != 0x9000) {
                            System.out.println("An error occurred with status: " + apdu.getStatus());
                        } else {
                            // Array copy: (src, offset, target, offset, copy size)
                            System.arraycopy(apdu.dataOut, 0, res, (i * 100), apdu.dataOut.length);
                        }
                    }
                    
                    if(allSend) {
                        apdu.command[Apdu.INS] = OsirisClient.INS_GET_FINGERPRINT;
                        apdu.command[Apdu.P1] = (byte) ((int)rnd);
                        apdu.command[Apdu.P2] = (byte) last;
                        cad.exchangeApdu(apdu);
                        if (apdu.getStatus() != 0x9000) {
                            System.out.println("An error occurred with status: " + apdu.getStatus());
                            break;
                        } else {
                            //array copy: (src, offset, target, offset, copy size)
                            System.arraycopy(apdu.dataOut, 0, res, (int)(rnd * 100 ), apdu.dataOut.length);
                        }
                    } else {
                        System.out.println("Fail to get all the part");
                    }
                    byte[] fgData = fileToByteArray("D:\\Card\\tecoright-two.dat");
                    // System.out.println("ReadLength : " + readLength);
                    System.out.println("Array equals : " + Arrays.equals(fgData, res));
                    break;
                case 10:
                        end = true;
                    break;
            }
        }
		
        // Turning off the card
        try {
            cad.powerDown();
        } catch (CadTransportException | IOException e) {
            System.out.println("Error sending powerDown command to Java Card");
        }		
    }
    
    public static void handleResponse(Apdu apdu) {
        if (apdu.getStatus() != 0x9000) {
            System.out.println("An error occurred with status: " + apdu.getStatus());
        } else {
            System.out.println("OK");
        }
    }
    
    private static byte[] fileToByteArray(String filePath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int readCount = 0;

            while ((readCount = fis.read(buffer)) != -1){
                baos.write(buffer, 0, readCount);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[] { };
    }
}
