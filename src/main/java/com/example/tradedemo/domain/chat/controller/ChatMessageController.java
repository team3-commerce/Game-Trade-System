package com.example.tradedemo.domain.chat.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.config.ChatRedisPublisher;
import com.example.tradedemo.domain.chat.dto.ChatMessageRequest;
import com.example.tradedemo.domain.chat.dto.RedisChatMessageRequest;
import com.example.tradedemo.domain.chat.entity.ChatMessage;
import com.example.tradedemo.domain.chat.entity.ChatRoom;
import com.example.tradedemo.domain.chat.repository.ChatMessageRepository;
import com.example.tradedemo.domain.chat.repository.ChatRoomRepository;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
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
                .findByIdWithListing(request.getRoomId())
                .orElseThrow();

        // 메시지 전송 시마다 상품 상태 검증
        if (room.getMarketListing() != null) {
            MarketListingStatus status = room.getMarketListing().getStatus();
            if (status != MarketListingStatus.SELLING) {
                String statusMessage = switch (status) {
                    case SOLD      -> "이 상품은 판매 완료된 상품입니다.";
                    case CLAIMED   -> "이 상품은 거래 완료된 상품입니다.";
                    case CANCELLED -> "이 상품은 취소된 상품입니다.";
                    case EXPIRED   -> "이 상품은 판매 기간이 만료된 상품입니다.";
                    default        -> "이 상품은 더 이상 채팅을 이용할 수 없는 상태입니다.";
                };

                RedisChatMessageRequest notice = new RedisChatMessageRequest(
                        room.getId(),
                        null,
                        "SYSTEM",
                        statusMessage
                );
                chatRedisPublisher.publish(room.getId(), notice);
                return;
            }
        }

        ChatMessage message = ChatMessage.create(sender, room, request.getContent());
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
    // 목록에서 채팅방 선택(구독)할 때 호출
    @MessageMapping("/chat.enter")
    public void enter(ChatMessageRequest request, Principal principal) {
        Member member = ((PrincipalDetails) principal).getMember();

        RedisChatMessageRequest systemMessage = new RedisChatMessageRequest(
                request.getRoomId(),
                null,
                "SYSTEM",
                member.getNickname() + "님이 입장했습니다."
        );

        chatRedisPublisher.publish(request.getRoomId(), systemMessage);
    }

    // 채팅방 퇴장 시스템 메시지
    // 다른 채팅방 선택 / 로그아웃 / 창 닫기 시 호출
    @MessageMapping("/chat.leave")
    public void leave(ChatMessageRequest request, Principal principal) {
        Member member = ((PrincipalDetails) principal).getMember();

        RedisChatMessageRequest systemMessage = new RedisChatMessageRequest(
                request.getRoomId(),
                null,
                "SYSTEM",
                member.getNickname() + "님이 퇴장했습니다."
        );

        chatRedisPublisher.publish(request.getRoomId(), systemMessage);
    }
}
