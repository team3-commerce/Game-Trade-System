package com.example.tradedemo.domain.chat.entity;

import com.example.tradedemo.domain.members.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
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

    private LocalDateTime createdAt= LocalDateTime.now();

    public ChatMessage(Member sender, ChatRoom chatRoom, String content) {
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}
