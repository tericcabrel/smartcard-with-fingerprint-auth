package com.tericcabrel.osiris.listeners;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.tericcabrel.osiris.models.SocketMessage;
import com.tericcabrel.osiris.utils.Messaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    private SimpMessageSendingOperations messagingTemplate;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");

        SocketMessage message = new SocketMessage();
        Channel channel = Messaging.getChannel();

        try {
            /***********************************************************************************************************/
            channel.queueDeclare(Messaging.Q_APPLET_SELECTED_RESPONSE, false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("[Q_APPLET_SELECTED_RESPONSE] Received '" + content + "'");
                message.setMessage(content);

                messagingTemplate.convertAndSend("/topic/cardInserted", message);
            };
            channel.basicConsume(Messaging.Q_APPLET_SELECTED_RESPONSE, true, deliverCallback, consumerTag -> { });

            /***********************************************************************************************************/
            channel.queueDeclare(Messaging.Q_CARD_REMOVED_RESPONSE, false, false, false, null);
            DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [Q_CARD_REMOVED_RESPONSE] Received '" + content + "'");
                message.setMessage(content);

                messagingTemplate.convertAndSend("/topic/cardRemoved", message);
            };
            channel.basicConsume(Messaging.Q_CARD_REMOVED_RESPONSE, true, deliverCallback1, consumerTag -> { });
            /***********************************************************************************************************/
            channel.queueDeclare(Messaging.Q_AUTHENTICATE_RESPONSE, false, false, false, null);
            DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [Q_AUTHENTICATE_RESPONSE] Received '" + content + "'");
                message.setMessage(content);

                messagingTemplate.convertAndSend("/topic/pinAuth", message);
            };
            channel.basicConsume(Messaging.Q_AUTHENTICATE_RESPONSE, true, deliverCallback2, consumerTag -> { });
            /***********************************************************************************************************/
            channel.queueDeclare(Messaging.Q_UNBLOCK_RESPONSE, false, false, false, null);
            DeliverCallback deliverCallback3 = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [Q_UNBLOCK_RESPONSE] Received '" + content + "'");
                message.setMessage(content);

                messagingTemplate.convertAndSend("/topic/cardUnblock", message);
            };
            channel.basicConsume(Messaging.Q_UNBLOCK_RESPONSE, true, deliverCallback3, consumerTag -> { });
            /***********************************************************************************************************/
            channel.queueDeclare(Messaging.Q_SET_DATA_RESPONSE, false, false, false, null);
            DeliverCallback deliverCallback4 = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [Q_SET_DATA_RESPONSE] Received '" + content + "'");
                message.setMessage(content);

                messagingTemplate.convertAndSend("/topic/cardSetData", message);
            };
            channel.basicConsume(Messaging.Q_SET_DATA_RESPONSE, true, deliverCallback4, consumerTag -> { });
            /***********************************************************************************************************/
            channel.queueDeclare(Messaging.Q_GET_DATA_RESPONSE, false, false, false, null);
            DeliverCallback deliverCallback5 = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [Q_GET_DATA_RESPONSE] Received '" + content + "'");
                message.setMessage(content);

                messagingTemplate.convertAndSend("/topic/cardGetData", message);
            };
            channel.basicConsume(Messaging.Q_GET_DATA_RESPONSE, true, deliverCallback5, consumerTag -> { });
            /***********************************************************************************************************/
            channel.queueDeclare(Messaging.Q_SET_NAME_RESPONSE, false, false, false, null);
            DeliverCallback deliverCallback6 = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [Q_SET_NAME_RESPONSE] Received '" + content + "'");
                message.setMessage(content);

                messagingTemplate.convertAndSend("/topic/cardSetName", message);
            };
            channel.basicConsume(Messaging.Q_SET_NAME_RESPONSE, true, deliverCallback6, consumerTag -> { });
            /***********************************************************************************************************/
            channel.queueDeclare(Messaging.Q_SET_BIRTH_DATE_RESPONSE, false, false, false, null);
            DeliverCallback deliverCallback7 = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [Q_SET_BIRTH_DATE_RESPONSE] Received '" + content + "'");
                message.setMessage(content);

                messagingTemplate.convertAndSend("/topic/cardSetBirth", message);
            };
            channel.basicConsume(Messaging.Q_SET_BIRTH_DATE_RESPONSE, true, deliverCallback7, consumerTag -> { });
            /***********************************************************************************************************/
            channel.queueDeclare(Messaging.Q_RESET_RESPONSE, false, false, false, null);
            DeliverCallback deliverCallback8 = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [Q_RESET_RESPONSE] Received '" + content + "'");
                message.setMessage(content);

                messagingTemplate.convertAndSend("/topic/cardReset", message);
            };
            channel.basicConsume(Messaging.Q_RESET_RESPONSE, true, deliverCallback8, consumerTag -> { });
            /***********************************************************************************************************/
            channel.queueDeclare(Messaging.Q_ENROLL_RESPONSE, false, false, false, null);
            DeliverCallback deliverCallback9 = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [Q_ENROLL_RESPONSE] Received '" + content + "'");
                message.setMessage(content);

                messagingTemplate.convertAndSend("/topic/enrollment", message);
            };
            channel.basicConsume(Messaging.Q_ENROLL_RESPONSE, true, deliverCallback9, consumerTag -> { });
            /***********************************************************************************************************/
            channel.queueDeclare(Messaging.Q_VERIFY_USER_RESPONSE, false, false, false, null);
            DeliverCallback deliverCallback10 = (consumerTag, delivery) -> {
                String content = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [Q_VERIFY_USER_RESPONSE] Received '" + content + "'");
                message.setMessage(content);

                messagingTemplate.convertAndSend("/topic/authFingerprint", message);
            };
            channel.basicConsume(Messaging.Q_VERIFY_USER_RESPONSE, true, deliverCallback10, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        //StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // String username = (String) headerAccessor.getSessionAttributes().get("username");
        // if(username != null) {
            // logger.info("User Disconnected : " + username);
            logger.info("User Disconnected : ");

            SocketMessage message = new SocketMessage();
            message.setCode("DSCT").setMessage("Bye");

            messagingTemplate.convertAndSend("/topic/greetings", message);
        // }
    }
}
