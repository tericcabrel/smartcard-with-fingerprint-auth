package com.tericcabrel.services;

import com.tericcabrel.utils.Helpers;

import javax.smartcardio.*;
import java.util.HashMap;

public class OsirisCardService {
    /* Constants */
    private static byte[] APPLET_AID = { (byte) 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00 };
    public static final byte CLA_OSIRIS = (byte) 0x3A;

    private static final byte INS_GET_DATA = 0x00;
    private static final byte INS_SET_DATA = 0x01;
    private static final byte INS_SET_NAME = 0x02;
    private static final byte INS_SET_BIRTH_DATE = 0x03;
    private static final byte INS_RESET_DATA = 0x04;
    private final static byte INS_PIN_AUTH = (byte) 0x05;
    private final static byte INS_PIN_UNBLOCK = (byte) 0x06;
    private final static byte INS_SET_FINGERPRINT = (byte) 0x07;
    private final static byte INS_GET_FINGERPRINT = (byte) 0x08;

    public static final String DATA_DELIMITER = "\\|";

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

    private static Card card;
    private static int pinRemaining = 3;
    private static boolean appletSelected = false;
    private static boolean isAuthenticated = false;

    public static String selectApplet() {
        // The applet is the default on the card so it's not needed to selected
        // Send Select Applet command
        /* try {
            ResponseAPDU response = card.getBasicChannel().transmit(new CommandAPDU(0x00, 0xA4, 0x04, 0x00, APPLET_AID));

            if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                appletSelected = true;
                return SW_APPLET_SELECTED;
            }

            return String.valueOf(response.getSW());
        } catch (CardException e) {
            e.printStackTrace();
        }

        return SW_INTERNAL_ERROR;*/
        return SW_APPLET_SELECTED;
    }

    public static String authenticate(String pinCode) {
        try {
            byte[] data = Helpers.numberStringToByteArray(pinCode);

            ResponseAPDU response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_PIN_AUTH, 0x00, 0x00, data));

            if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                isAuthenticated = true;
                pinRemaining = 3;
            }

            return String.valueOf(response.getSW());
        } catch (CardException e) {
            e.printStackTrace();
        }

        return SW_INTERNAL_ERROR;
    }

    public static String getData() {
        try {
            ResponseAPDU response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_GET_DATA, 0x00, 0x00));
            if (String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                return Helpers.byteArrayToString(response.getData());
            }

            return String.valueOf(response.getSW());
        } catch (CardException e) {
            e.printStackTrace();
        }

        return SW_INTERNAL_ERROR;
    }

    public static String setData(String data) {
        try {
            byte[] params = data.getBytes();

            ResponseAPDU response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_SET_DATA, 0x00, 0x00, params));

            return String.valueOf(response.getSW());
        } catch (CardException e) {
            e.printStackTrace();
        }

        return SW_INTERNAL_ERROR;
    }

    public static String setName(String data) {
        try {
            byte[] params = data.getBytes();

            ResponseAPDU response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_SET_NAME, 0x00, 0x00, params));

            return String.valueOf(response.getSW());
        } catch (CardException e) {
            e.printStackTrace();
        }

        return SW_INTERNAL_ERROR;
    }

    public static String setBirthDate(String data) {
        try {
            byte[] params = data.getBytes();

            ResponseAPDU response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_SET_BIRTH_DATE, 0x00, 0x00, params));

            return String.valueOf(response.getSW());
        } catch (CardException e) {
            e.printStackTrace();
        }

        return SW_INTERNAL_ERROR;
    }

    public static String resetData() {
        try {
            ResponseAPDU response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_RESET_DATA, 0x00, 0x00));

            return String.valueOf(response.getSW());
        } catch (CardException e) {
            e.printStackTrace();
        }

        return SW_INTERNAL_ERROR;
    }

    public static String unblock() {
        try {
            ResponseAPDU response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_PIN_UNBLOCK, 0x00, 0x00));

            return String.valueOf(response.getSW());
        } catch (CardException e) {
            e.printStackTrace();
        }

        return SW_INTERNAL_ERROR;
    }

    public static String setFingerprint(byte[] fingerPrintTemplate) {
        byte[] Data = new byte[100];

        int fingerLength = fingerPrintTemplate.length;
        double round = Math.floor((double)fingerLength / 100);
        int lastPart = fingerLength % 100;

        try {
            byte[] lengthToByte = Helpers.prepareNumberForApdu(fingerLength);
            CommandAPDU cmd = new CommandAPDU(CLA_OSIRIS, INS_SET_FINGERPRINT, 0x00, 0x00, lengthToByte);
            ResponseAPDU response = card.getBasicChannel().transmit(cmd);

            if (!String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                return String.valueOf(response.getSW());
            }

            boolean allPartSend = true;
            for (int i = 0; i < round; i++) {
                // Array copy: (src, offset, target, offset, copy size)
                System.arraycopy(fingerPrintTemplate, (i * 100), Data, 0, 100);
                // System.out.println("Data Length: " + Data.length);

                cmd = new CommandAPDU(CLA_OSIRIS, INS_SET_FINGERPRINT, (byte) i, 0x64, Data);
                response = card.getBasicChannel().transmit(cmd);

                if (!String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                    allPartSend = false;
                    System.out.println("["+i+"] An error occurred with status: " + response.getSW());
                    break;
                }
            }

            if(allPartSend) {
                byte[] finalPart = new byte[lastPart];
                System.arraycopy(fingerPrintTemplate, (int) (round * 100), finalPart, 0, lastPart);

                cmd = new CommandAPDU(CLA_OSIRIS, INS_SET_FINGERPRINT, (byte) round, (byte) lastPart, finalPart);
                response = card.getBasicChannel().transmit(cmd);

                return String.valueOf(response.getSW());
            }

            return String.valueOf(response.getSW());
        } catch (CardException e) {
            e.printStackTrace();
        }

        return "15000";
    }

    public static byte[] getFingerpint(int length) {
        byte[] result = new byte[length];
        double round = Math.floor((double)length / 100);
        int last = length % 100;
        ResponseAPDU response;
        boolean allGet = true;

        try {
            for (int i = 0; i < round; i++) {
                response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_GET_FINGERPRINT, (byte) i, 0x64));
                if (!String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                    System.out.println("An error occurred with status: " + response.getSW());
                    allGet = false;
                    break;
                } else {
                    // Array copy: (src, offset, target, offset, copy size)
                    System.arraycopy(response.getData(), 0, result, (i * 100), response.getData().length);
                }
            }

            if (allGet) {
                response = card.getBasicChannel().transmit(new CommandAPDU(CLA_OSIRIS, INS_GET_FINGERPRINT, (byte) round, (byte) last));
                if (!String.valueOf(response.getSW()).equals(SW_SUCCESS_RESPONSE)) {
                    System.out.println("An error occurred with status: " + response.getSW());
                } else {
                    //array copy: (src, offset, target, offset, copy size)
                    System.arraycopy(response.getData(), 0, result, (int) (round * 100), response.getData().length);
                }
            } else {
                System.out.println("Fail to get all the part");
            }
        } catch (CardException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void disconnect() {
        if (card != null) {
            try {
                card.disconnect(true);
            } catch (CardException e) {
                e.printStackTrace();
            }
        }
    }

    public static Card getCard() {
        return card;
    }

    public static void setCard(Card newCard) {
        card = newCard;
    }

    public int getPinRemaining() {
        return pinRemaining;
    }

    public void setPinRemaining(int pinRemaining) {
        OsirisCardService.pinRemaining = pinRemaining;
    }

    public boolean isAppletSelected() {
        return appletSelected;
    }

    public void setAppletSelected(boolean appletSelected) {
        OsirisCardService.appletSelected = appletSelected;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }
}
