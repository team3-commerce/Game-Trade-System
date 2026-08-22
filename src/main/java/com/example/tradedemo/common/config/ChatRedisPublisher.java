package com.example.tradedemo.common.config;

import com.example.tradedemo.domain.chat.dto.RedisChatMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatRedisPublisher {
    // 메시지 수신 → Redis 채널에 발행, 발송하는 부분
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(Long roomId, RedisChatMessageRequest message) {
        String topic = "chat-room:" + roomId;
        redisTemplate.convertAndSend(topic, message);
    }
}
