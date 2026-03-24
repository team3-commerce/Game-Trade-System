package com.example.tradedemo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisSubscriberConfig {

    // 동적 채널 구독
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory, ChatRedisSubscriber subscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(
                subscriber,
                new PatternTopic("chat-room:*")
                // PatternTopic = 패턴은 너무 무거워 ElastiCache 가 안된다 함 : 분산구조라 Redis를 전체를 훑는 Patten은 안 됨.
                // 그러니
        );

        return container;
    }
}
