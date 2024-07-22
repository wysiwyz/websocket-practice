package com.example.websocket.model;

import lombok.Getter;
import lombok.Setter;

/**
 * The message payload that will be exchanged between the clients and the server
 */
@Getter
@Setter
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
