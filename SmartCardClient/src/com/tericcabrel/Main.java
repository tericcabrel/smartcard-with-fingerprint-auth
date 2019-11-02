package com.tericcabrel;

import javax.smartcardio.*;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    /* Constants */
    public static final byte CLA_OSIRIS = (byte) 0x3A;

    public static final byte INS_GET_DATA = 0x00;
    public static final byte INS_SET_DATA = 0x01;
    public static final byte INS_SET_NAME = 0x02;
    public static final byte INS_SET_BIRTHDATE = 0x03;
    public static final byte INS_RESET_DATA = 0x04;
    private final static byte INS_PIN_AUTH = (byte) 0x05;
    private final static byte INS_PIN_UNBLOCK = (byte) 0x06;
    private final static byte INS_FINGERPRINT = (byte) 0x07;

    // Signal that there is no error
    public final static String SW_SUCCESS_RESPONSE = "36864";

    // Signal that the applet selected successfully
    public final static String SW_APPLET_SELECTED = "36865";

    // Signal that the card was removed
    public final static String SW_CARD_REMOVED = "14000";

    // Signal that an unknown error occurred
    private final static String SW_INTERNAL_ERROR = "15000";
    // Signal that the PIN verification failed
    private final static String SW_VERIFICATION_FAILED = "25344";
    // Signal the PIN validation is required for an action
    private final static String SW_PIN_VERIFICATION_REQUIRED = "25345";

    private static byte[] APPLET_AID = { (byte) 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00 };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, CardException {
        Card card = null;

        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        List<CardTerminal> cardTerminals = null;
        try {
            cardTerminals = terminalFactory.terminals().list();

            if (cardTerminals.isEmpty()) {
                System.out.println("Skipping the test: no card terminals available");
                return;
            }

            // System.out.println("Terminals: " + cardTerminals);
            CardTerminal cardTerminal = cardTerminals.get(0);

            if (cardTerminal.isCardPresent()) {
                card = cardTerminal.connect("T=1");
                System.out.println("Connected to the card!");
            }
        } catch (CardException e) {
            e.printStackTrace();
        }

        /*try {
            ResponseAPDU response = card.getBasicChannel().transmit(
                    new CommandAPDU(CLA_OSIRIS, 0xA4, 0x04, 0x00, APPLET_AID)
            );

            if (!String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                System.out.println("Error while selecting the applet: " + response.getSW());
                System.exit(1);
            }
        } catch (CardException e) {
            e.printStackTrace();
            System.exit(1);
        }*/

        // Main menu
        Scanner sc = new Scanner(System.in);
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
            System.out.println("9 - QUIT");
            System.out.println();
            System.out.println("Your choice: ");

            int choice = System.in.read();
            while (!(choice >= '1' && choice <= '9')) {
                choice = System.in.read();
            }

            ResponseAPDU response;
            byte[] data;
            sc.nextLine();
            switch (choice) {
                case '1':
                    System.out.print("Enter the PIN Code: ");
                    String pin = sc.nextLine();
                    System.out.println(pin);
                    data = Helpers.numberStringToByteArray(pin);

                    response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_PIN_AUTH, 0x00, 0x00, data));

                    if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                        System.out.println("Success!!!");
                    } else {
                        System.out.println("Authentication failed : Invalid PIN Code");
                    }
                    break;
                case '2':
                    response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_GET_DATA, 0x00, 0x00));
                    if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                        System.out.print("\nData : " + Helpers.byteArrayToString(response.getData()));
                    } else {
                        System.out.println("An error occurred with status: " + response.getSW());
                    }
                    break;
                case '3':
                    System.out.print("Enter the info (<uid>|<name>|<birth>): ");
                    String info = sc.nextLine();
                    data = info.getBytes();
                    response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_SET_DATA, 0x00, 0x00, data));

                    handleResponse(response);
                    break;
                case '4':
                    System.out.print("Enter the name: ");
                    String name = sc.nextLine();
                    byte[] nameData = name.getBytes();

                    response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_SET_NAME, 0x00, 0x00, nameData));

                    handleResponse(response);
                    break;
                case '5':
                    System.out.print("Enter the birth date (YYYY-MM-DD) : ");
                    String birth = sc.nextLine();
                    byte[] birthData = birth.getBytes();

                    response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_SET_BIRTHDATE, 0x00, 0x00, birthData));

                    handleResponse(response);
                    break;
                case '6':
                    response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_RESET_DATA, 0x00, 0x00));

                    handleResponse(response);
                    break;
                case '7':
                    response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_PIN_UNBLOCK, 0x00, 0x00));

                    handleResponse(response);
                    break;
                case '8':
                    byte[] fingerData = fileToByteArray("D:\\Card\\tecoright.dat");
                    byte[] Data = new byte[100];

                    System.out.println("Finger Length: " + fingerData.length);

                    int fingerLength = fingerData.length;
                    double round = Math.floor((double)fingerLength / 100);
                    int lastPart = fingerLength % 100;

                    System.out.println("Round : " + round);
                    System.out.println("Last Part : " + lastPart);

                    byte[] dest = new byte[fingerLength];

                    for (int i = 0; i < round; i++) {
                        // array copy: (src, offset, target, offset, copy size)
                        System.arraycopy(fingerData, (i * 100), Data, 0, 100);
                        System.out.println("Data Length: " + Data.length);
                        System.arraycopy(Data, 0, dest, (i * 100), 100);
                    }
                    System.arraycopy(fingerData,  (int) (round * 100), dest, (int) (round * 100), lastPart);

                    byte[] fake = new byte[fingerLength];
                    System.arraycopy(fingerData, 0, fake, 0, fingerLength);

                    System.out.println("Dest Length : " + dest.length);
                    System.out.println("Array equals : " + Arrays.equals(fingerData, dest));
                    System.out.println("Array equals : " + Arrays.equals(fingerData, fake));

                    response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_FINGERPRINT, 0x00, 0x00, Data, 0, Data.length));

                    handleResponse(response);
                    break;
                case '9':
                    end = true;
                    break;
            }

            if (choice != '9') {
                sc.nextLine();
                System.out.print("\nContinue ? (Y/N) : ");
                String str = sc.nextLine();

                if (!str.toLowerCase().equals("y")) {
                    end = true;
                }
            }
            System.out.println();
        }

        // Turning off the card
        card.disconnect(true);
        System.out.println("Card disconnected");
    }

    private static void handleResponse(ResponseAPDU apdu) {
        if (apdu.getSW() != Integer.parseInt(SW_SUCCESS_RESPONSE)) {
            System.out.println("An error occurred with status: " + apdu.getSW());
        } else {
            System.out.println("Success");
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
