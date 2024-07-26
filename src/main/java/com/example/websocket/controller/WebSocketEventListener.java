package com.example.websocket.controller;

import com.example.websocket.model.ChatMessage;
import com.example.websocket.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

/**
 * To listen for socket connect and disconnect events for log and broadcast purpose
 */
@Slf4j
public class WebSocketEventListener {
    @Value("${redis.channel.userStatus}")
    private String userStatus;
    @Value("${redis.set.onlineUsers}")
    private String onlineUsers;
    private SimpMessageSendingOperations messagingTemplate;
    private RedisTemplate<String, String> redisTemplate;

    WebSocketEventListener(SimpMessageSendingOperations messagingTemplate, RedisTemplate<String, String> redisTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        if (username!=null) {
            log.info("User Disconnected: {}", username);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);
//            messagingTemplate.convertAndSend("/topic/public", chatMessage);
            try{
                headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
                redisTemplate.opsForSet().add(onlineUsers, chatMessage.getSender());
                redisTemplate.convertAndSend(userStatus, JsonUtil.parseObjToJson(chatMessage));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
