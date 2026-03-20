package com.example.tradedemo.domain.chat.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.config.ChatRedisPublisher;
import com.example.tradedemo.domain.chat.dto.ChatMessageRequest;
import com.example.tradedemo.domain.chat.dto.RedisChatMessageRequest;
import com.example.tradedemo.domain.chat.entity.ChatMessage;
import com.example.tradedemo.domain.chat.entity.ChatRoom;
import com.example.tradedemo.domain.chat.repository.ChatMessageRepository;
import com.example.tradedemo.domain.chat.repository.ChatRoomRepository;
import com.example.tradedemo.domain.members.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRedisPublisher chatRedisPublisher;
    private final SimpMessagingTemplate messagingTemplate;

    // WebSocket 연결을 통해 메세지 발송
    @MessageMapping("/chat.send")
    public void send(ChatMessageRequest request, Principal principal) {

        Member sender = ((PrincipalDetails) principal).getMember();

        ChatRoom room = chatRoomRepository
                .findById(request.getRoomId())
                .orElseThrow();

        ChatMessage message= new ChatMessage(sender, room, request.getContent());
        chatMessageRepository.save(message);

        RedisChatMessageRequest redisMessage = new RedisChatMessageRequest(
                message.getChatRoom().getId(),
                message.getSender().getId(),
                message.getSender().getNickname(),
                message.getContent()
        );

        chatRedisPublisher.publish(room.getId(), redisMessage);
    }

    // 채팅방 입장 시스템 메시지
    // 목록에서 채팅방 선택(구독)할 때 호출, 입장 메시지는 DB 저장 X
    @MessageMapping("/chat.enter")
    public void enter(ChatMessageRequest request, Principal principal) {
        Member member = ((PrincipalDetails) principal).getMember();

        RedisChatMessageRequest systemMessage = new RedisChatMessageRequest(
                request.getRoomId(),
                null,                    // senderId null → 시스템 메시지 구분자
                "SYSTEM",
                member.getNickname() + "님이 입장했습니다."
        );

        chatRedisPublisher.publish(request.getRoomId(), systemMessage);
    }
}
