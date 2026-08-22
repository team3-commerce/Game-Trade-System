package com.example.tradedemo.domain.chat.entity;

import com.example.tradedemo.domain.members.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    private String content;

    private LocalDateTime createdAt;

    public static ChatMessage create(Member sender, ChatRoom chatRoom, String content) {
        ChatMessage message = new ChatMessage();
        message.sender = sender;
        message.chatRoom = chatRoom;
        message.content = content;
        message.createdAt = LocalDateTime.now();
        return message;
    }
}
