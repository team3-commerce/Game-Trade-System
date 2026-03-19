package com.example.tradedemo.domain.chat.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.domain.chat.dto.ChatMessageRequest;
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
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
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

        messagingTemplate.convertAndSend("/sub/chat/" + room.getId(), request);
    }
}
