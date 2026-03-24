package com.example.tradedemo.common.config;

import com.example.tradedemo.domain.chat.dto.RedisChatMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRedisSubscriber implements MessageListener {
    // 각 서버는 Redis로 받은 메시지를 자신의 클라이언트에게 다시 전달

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 구독한 채널에서 메시지를 받아서 어떻게 처리할 것인지
        // 외부에서 받은 Redis 메시지를 Spring Boot가 이해할 수 있는 메시지로 변환하는 과정
        RedisChatMessageRequest redisMessage = null;
        try {
            redisMessage = objectMapper.readValue(
                    message.getBody(),
                    RedisChatMessageRequest.class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("메시지 받았음");
        log.info(redisMessage.getContent());

        // Redis 채널에서 수신 → 자기 서버의 STOMP 구독자에게 전달
        messagingTemplate.convertAndSend(
                "/sub/chat/" + redisMessage.getRoomId(),
                redisMessage);
    }
}
