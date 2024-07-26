package com.example.websocket.service;

import com.example.websocket.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatService {
    private SimpMessageSendingOperations simpMessageSendingOperations;

    ChatService(SimpMessageSendingOperations simpMessageSendingOperations) {
        this.simpMessageSendingOperations = simpMessageSendingOperations;
    }

    public void sendMsg(@Payload ChatMessage chatMessage) {
        log.info("Send message by simpMessageSendingOperations: {}", chatMessage.toString());
        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
    }

    public void alertUserStatus(@Payload ChatMessage chatMessage) {
        log.info("Alert user online by simpMessageSendingOperations: {}", chatMessage.toString());
        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
    }
}
