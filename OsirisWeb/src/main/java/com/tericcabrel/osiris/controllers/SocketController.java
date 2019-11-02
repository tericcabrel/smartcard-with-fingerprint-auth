package com.tericcabrel.osiris.controllers;

import com.rabbitmq.client.Channel;
import com.tericcabrel.osiris.models.SocketMessage;
import com.tericcabrel.osiris.utils.Messaging;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.nio.charset.StandardCharsets;

@Controller
public class SocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public SocketMessage send(SocketMessage message) throws Exception {

        Thread.sleep(1000); // simulated delay

        return message;
    }

    @MessageMapping("/pinAuthentication")
    public void pinAuthentication(SocketMessage message) throws Exception {
        Channel channel = Messaging.getChannel();

        channel.queueDeclare(Messaging.Q_AUTHENTICATE_REQUEST, false, false, false, null);
        channel.basicPublish("", Messaging.Q_AUTHENTICATE_REQUEST, null, message.getMessage().getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent to queue " + Messaging.Q_AUTHENTICATE_REQUEST);
    }

    @MessageMapping("/cardUnblock")
    public void pinUnblock(SocketMessage message) throws Exception {
        Channel channel = Messaging.getChannel();

        channel.queueDeclare(Messaging.Q_UNBLOCK_REQUEST, false, false, false, null);
        channel.basicPublish("", Messaging.Q_UNBLOCK_REQUEST, null, message.getMessage().getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent to queue " + Messaging.Q_UNBLOCK_REQUEST);
    }

    @MessageMapping("/cardSetData")
    public void cardSetData(SocketMessage message) throws Exception {
        Channel channel = Messaging.getChannel();

        channel.queueDeclare(Messaging.Q_SET_DATA_REQUEST, false, false, false, null);
        channel.basicPublish("", Messaging.Q_SET_DATA_REQUEST, null, message.getMessage().getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent to queue " + Messaging.Q_SET_DATA_REQUEST);
    }

    @MessageMapping("/cardGetData")
    public void cardGetData(SocketMessage message) throws Exception {
        Channel channel = Messaging.getChannel();

        channel.queueDeclare(Messaging.Q_GET_DATA_REQUEST, false, false, false, null);
        channel.basicPublish("", Messaging.Q_GET_DATA_REQUEST, null, message.getMessage().getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent to queue " + Messaging.Q_GET_DATA_REQUEST);
    }

    @MessageMapping("/cardSetName")
    public void cardSetName(SocketMessage message) throws Exception {
        Channel channel = Messaging.getChannel();

        channel.queueDeclare(Messaging.Q_SET_NAME_REQUEST, false, false, false, null);
        channel.basicPublish("", Messaging.Q_SET_NAME_REQUEST, null, message.getMessage().getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent to queue " + Messaging.Q_SET_NAME_REQUEST);
    }

    @MessageMapping("/cardSetBirth")
    public void cardSetBirth(SocketMessage message) throws Exception {
        Channel channel = Messaging.getChannel();

        channel.queueDeclare(Messaging.Q_SET_BIRTH_DATE_REQUEST, false, false, false, null);
        channel.basicPublish("", Messaging.Q_SET_BIRTH_DATE_REQUEST, null, message.getMessage().getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent to queue " + Messaging.Q_SET_BIRTH_DATE_REQUEST);
    }

    @MessageMapping("/cardReset")
    public void cardReset(SocketMessage message) throws Exception {
        Channel channel = Messaging.getChannel();

        channel.queueDeclare(Messaging.Q_RESET_REQUEST, false, false, false, null);
        channel.basicPublish("", Messaging.Q_RESET_REQUEST, null, message.getMessage().getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent to queue " + Messaging.Q_RESET_REQUEST);
    }

    @MessageMapping("/enrollment")
    public void enrollment(SocketMessage message) throws Exception {
        Channel channel = Messaging.getChannel();

        channel.queueDeclare(Messaging.Q_ENROLL_REQUEST, false, false, false, null);
        channel.basicPublish("", Messaging.Q_ENROLL_REQUEST, null, message.getMessage().getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent to queue " + Messaging.Q_ENROLL_REQUEST);
    }

    @MessageMapping("/authFingerprint")
    public void fingerprintAuthentication(SocketMessage message) throws Exception {
        Channel channel = Messaging.getChannel();

        channel.queueDeclare(Messaging.Q_VERIFY_USER_REQUEST, false, false, false, null);
        channel.basicPublish("", Messaging.Q_VERIFY_USER_REQUEST, null, message.getMessage().getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent to queue " + Messaging.Q_VERIFY_USER_REQUEST);
    }
}
