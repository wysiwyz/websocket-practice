package com.example.websocket.redis;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * Redis 訂閱頻道屬性類
 * */
@Slf4j
@Component
public class RedisListenerBean {
    @Value("${server.port}")
    private String serverPort;
    @Value("${redis.channel.msgToAll}")
    private String msgToAll;
    @Value("${redis.channel.userStatus}")
    private String userStatus;

    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // listen to msgToAll
        container.addMessageListener(listenerAdapter, new PatternTopic(msgToAll));
        log.info("Subscribed Redis channel: {}", msgToAll);
        container.addMessageListener(listenerAdapter, new PatternTopic(userStatus));
        log.info("Subscribed Redis channel: {}", userStatus);
        return container;
    }

}
