package com.example.tradedemo.domain.chat.dto;

import com.example.tradedemo.domain.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {
    private Long messageId;
    private String content;
    private Long senderId;
    private String senderNickname;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getContent(),
                message.getSender().getId(),
                message.getSender().getNickname(),
                message.getCreatedAt()
        );
    }
}
