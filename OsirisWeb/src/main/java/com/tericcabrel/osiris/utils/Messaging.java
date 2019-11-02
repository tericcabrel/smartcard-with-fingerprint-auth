package com.tericcabrel.osiris.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Messaging {
    public final static String Q_APPLET_SELECTED_RESPONSE = "Q_APPLET_SELECTED_RESPONSE";
    public final static String Q_CARD_REMOVED_RESPONSE = "Q_CARD_REMOVED_RESPONSE";

    public final static String Q_AUTHENTICATE_REQUEST = "Q_AUTHENTICATE_REQUEST";
    public final static String Q_AUTHENTICATE_RESPONSE = "Q_AUTHENTICATE_RESPONSE";

    public final static String Q_GET_DATA_REQUEST = "Q_GET_DATA_REQUEST";
    public final static String Q_GET_DATA_RESPONSE = "Q_GET_DATA_RESPONSE";

    public final static String Q_SET_DATA_REQUEST = "Q_SET_DATA_REQUEST";
    public final static String Q_SET_DATA_RESPONSE = "Q_SET_DATA_RESPONSE";

    public final static String Q_SET_NAME_REQUEST = "Q_SET_NAME_REQUEST";
    public final static String Q_SET_NAME_RESPONSE = "Q_SET_NAME_RESPONSE";

    public final static String Q_SET_BIRTH_DATE_REQUEST = "Q_SET_BIRTH_DATE_REQUEST";
    public final static String Q_SET_BIRTH_DATE_RESPONSE = "Q_SET_BIRTH_DATE_RESPONSE";

    public final static String Q_RESET_REQUEST = "Q_RESET_REQUEST";
    public final static String Q_RESET_RESPONSE = "Q_RESET_RESPONSE";

    public final static String Q_UNBLOCK_REQUEST = "Q_UNBLOCK_REQUEST";
    public final static String Q_UNBLOCK_RESPONSE = "Q_UNBLOCK_RESPONSE";

    public final static String Q_DISCONNECT_REQUEST = "Q_DISCONNECT_REQUEST";
    public final static String Q_DISCONNECT_RESPONSE = "Q_DISCONNECT_RESPONSE";

    public static String Q_ENROLL_REQUEST = "Q_ENROLL_REQUEST";
    public static String Q_ENROLL_RESPONSE = "Q_ENROLL_RESPONSE";

    public final static String Q_GET_FINGERPRINT_REQUEST = "Q_GET_FINGERPRINT_REQUEST";
    public final static String Q_GET_FINGERPRINT_RESPONSE = "Q_GET_FINGERPRINT_RESPONSE";

    public final static String Q_VERIFY_USER_REQUEST = "Q_VERIFY_REQUEST";
    public final static String Q_VERIFY_USER_RESPONSE = "Q_VERIFY_RESPONSE";

    public final static String Q_MATCH_MATCH_FINGERPRINT_REQUEST = "Q_VERIFY_REQUEST";
    public final static String Q_MATCH_FINGERPRINT_RESPONSE = "Q_VERIFY_RESPONSE";


    // Signal that there is no error
    public final static String SW_SUCCESS_RESPONSE = "36864";
    // Signal that the applet selected successfully
    public final static String SW_APPLET_SELECTED = "36865";
    // Signal that the card was removed
    public final static String SW_CARD_REMOVED = "14000";
    // Signal that an unknown error occurred
    public final static String SW_INTERNAL_ERROR = "15000";
    // Signal that the PIN verification failed
    public final static String SW_VERIFICATION_FAILED = "25344";
    // Signal the PIN validation is required for an action
    public final static String SW_PIN_VERIFICATION_REQUIRED = "25345";

    private static Connection connection;
    private static Channel channel;

    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        }

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
        }

        return connection;
    }

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }

        if (connection != null) {
            try {
                return getConnection().createChannel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return channel;
    }
}
