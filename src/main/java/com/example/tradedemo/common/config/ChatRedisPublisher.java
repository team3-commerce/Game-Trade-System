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

    // 패턴 -> id 취소 Long roomId
    public void publish(RedisChatMessageRequest message) {
        // String topic = "chat-room:" + roomId;
        // redisTemplate.convertAndSend(topic, message);

        // roomId를 message에서 꺼냄
        redisTemplate.convertAndSend("chat-room:" + message.getRoomId(), message);

    }
}
