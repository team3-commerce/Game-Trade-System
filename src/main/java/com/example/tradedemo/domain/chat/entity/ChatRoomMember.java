package com.example.tradedemo.domain.chat.entity;

import com.example.tradedemo.domain.chat.enums.ChatRoomMemberRole;
import com.example.tradedemo.domain.members.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "chat_room_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chat_room_member",
                        columnNames = {"chat_room_id", "member_id"}
                )
        }
)
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomMemberRole role;

    private LocalDateTime joinedAt;

    public static ChatRoomMember create(ChatRoom chatRoom, Member member, ChatRoomMemberRole role) {
        ChatRoomMember chatRoomMember = new ChatRoomMember();
        chatRoomMember.chatRoom = chatRoom;
        chatRoomMember.member = member;
        chatRoomMember.role = role;
        chatRoomMember.joinedAt = LocalDateTime.now();
        return chatRoomMember;
    }
}
