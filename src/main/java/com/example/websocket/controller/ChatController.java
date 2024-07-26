package com.example.websocket.controller;

import com.example.websocket.model.ChatMessage;
import com.example.websocket.util.JsonUtil;
import com.rabbitmq.tools.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Objects;

/**
 * Responsible for receiving messages from one client and broadcasting to others
 */
@Slf4j
@Controller
public class ChatController {
    @Value("${redis.channel.msgToAll}")
    private String msgToAll;
    @Value("${redis.channel.userStatus}")
    private String userStatus;
    @Value("${redis.set.onlineUsers}")
    private String onlineUsers;
    private RedisTemplate<String, String> redisTemplate;

    ChatController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    /**
     * forward message to Redis, instead of sending msg to frontend, we let the server listen to Redis message, then send messages
     * @param chatMessage message with destination /app/chat.sendMessage
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public void sendMessage(@Payload ChatMessage chatMessage) {
//        return chatMessage;
        try {
            redisTemplate.convertAndSend(msgToAll, JsonUtil.parseObjToJson(chatMessage));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Add user
     * @param chatMessage message with destination /app/chat.addUser
     * @param headerAccessor
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
//        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessage.getSender());
        log.info("User added in chatroom: {}", chatMessage.getSender());
        try {
            Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", chatMessage.getSender());
            redisTemplate.opsForSet().add(onlineUsers, chatMessage.getSender());
            redisTemplate.convertAndSend(userStatus, JsonUtil.parseObjToJson(chatMessage));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
