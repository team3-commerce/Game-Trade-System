package com.example.tradedemo.domain.chat.dto;

import com.example.tradedemo.domain.chat.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomResponse(Long id, String name, String displayName, LocalDateTime createdAt) {

    // DB에 저장된 이름 그대로 사용
    public static ChatRoomResponse from(ChatRoom room) {
        return new ChatRoomResponse(room.getId(), room.getName(), room.getName(), room.getCreatedAt());
    }

    // 상대방 닉네임으로 displayName을 동적으로 지정
    // → name: DB 저장값 (생성자 기준 이름)
    // → displayName: 조회자 기준 상대방 닉네임 + "과의 채팅"
    public static ChatRoomResponse of(ChatRoom room, String otherNickname) {
        String displayName = otherNickname + "와의 채팅";
        return new ChatRoomResponse(room.getId(), room.getName(), displayName, room.getCreatedAt());
    }
}
