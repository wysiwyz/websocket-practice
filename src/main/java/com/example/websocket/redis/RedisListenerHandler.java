package com.example.websocket.redis;

import com.example.websocket.model.ChatMessage;
import com.example.websocket.service.ChatService;
import com.example.websocket.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

/** Redis subscription channel handling */
@Slf4j
@Component
public class RedisListenerHandler extends MessageListenerAdapter {
    @Value("${redis.channel.msgToAll}")
    private String msgToAll;
    @Value("${server.port}")
    private String serverPort;
    @Value("${redis.channel.userStatus}")
    private String userStatus;
    private RedisTemplate<String, String> redisTemplate;
    private ChatService chatService;

    RedisListenerHandler(RedisTemplate<String, String> redisTemplate, ChatService chatService) {
        this.redisTemplate = redisTemplate;
        this.chatService = chatService;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        byte[] body = message.getBody();
        byte[] channel = message.getChannel();
        String rawMsg = "";
        String topic = "";
        try {
            rawMsg = redisTemplate.getStringSerializer().deserialize(body);
            topic = redisTemplate.getStringSerializer().deserialize(channel);
            log.info("Received raw message from topic: {}, raw message content: {}", topic, rawMsg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (msgToAll.equals(topic)) {
            log.info("Send message to all users: {}", rawMsg);
            ChatMessage chatMessage = JsonUtil.parseJsonToObj(rawMsg, ChatMessage.class);
            // 發送消息給所有在線Cid
            if (chatMessage != null) {
                chatService.sendMsg(chatMessage);
            }
        } else if (userStatus.equals(topic)) {
            ChatMessage chatMessage = JsonUtil.parseJsonToObj(rawMsg, ChatMessage.class);
            if (chatMessage != null) {
                chatService.alertUserStatus(chatMessage);
            }
        } else {
            log.warn("No further operation with this topic!");
        }
    }
}
