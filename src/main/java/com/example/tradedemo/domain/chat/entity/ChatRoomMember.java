package com.example.tradedemo.domain.chat.entity;

import com.example.tradedemo.domain.members.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
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
    // ChatRoom은 참여자 필드가 없기 때문에 ChatRoom과 Member 연결하는 테이블
    // 참여자 테이블 -> 내 채팅방 목록 조회
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private LocalDateTime joinedAt = LocalDateTime.now();

    public ChatRoomMember(ChatRoom chatRoom, Member member) {
        this.chatRoom = chatRoom;
        this.member = member;
        this.joinedAt = LocalDateTime.now();
    }
}
