package com.tericcabrel.utils;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author ZEGEEK
 */
public class Messaging {
    public static String Q_APPLET_SELECTED_RESPONSE = "Q_APPLET_SELECTED_RESPONSE";
    public static String Q_CARD_REMOVED_RESPONSE = "Q_CARD_REMOVED_RESPONSE";

    public static String Q_AUTHENTICATE_REQUEST = "Q_AUTHENTICATE_REQUEST";
    public static String Q_AUTHENTICATE_RESPONSE = "Q_AUTHENTICATE_RESPONSE";

    public static String Q_GET_DATA_REQUEST = "Q_GET_DATA_REQUEST";
    public static String Q_GET_DATA_RESPONSE = "Q_GET_DATA_RESPONSE";

    public static String Q_SET_DATA_REQUEST = "Q_SET_DATA_REQUEST";
    public static String Q_SET_DATA_RESPONSE = "Q_SET_DATA_RESPONSE";

    public static String Q_SET_NAME_REQUEST = "Q_SET_NAME_REQUEST";
    public static String Q_SET_NAME_RESPONSE = "Q_SET_NAME_RESPONSE";

    public static String Q_SET_BIRTH_DATE_REQUEST = "Q_SET_BIRTH_DATE_REQUEST";
    public static String Q_SET_BIRTH_DATE_RESPONSE = "Q_SET_BIRTH_DATE_RESPONSE";

    public static String Q_RESET_REQUEST = "Q_RESET_REQUEST";
    public static String Q_RESET_RESPONSE = "Q_RESET_RESPONSE";

    public static String Q_UNBLOCK_REQUEST = "Q_UNBLOCK_REQUEST";
    public static String Q_UNBLOCK_RESPONSE = "Q_UNBLOCK_RESPONSE";

    public static String Q_DISCONNECT_REQUEST = "Q_DISCONNECT_REQUEST";
    public static String Q_DISCONNECT_RESPONSE = "Q_DISCONNECT_RESPONSE";

    public static String Q_ENROLL_REQUEST = "Q_ENROLL_REQUEST";
    public static String Q_ENROLL_RESPONSE = "Q_ENROLL_RESPONSE";

    public static String Q_VERIFY_USER_REQUEST = "Q_VERIFY_REQUEST";
    public static String Q_VERIFY_USER_RESPONSE = "Q_VERIFY_RESPONSE";

    public static void sendToQueue(Channel channel, String queueName, String message) {
        try {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent to queue " + queueName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
