package com.example.tradedemo.domain.chat.dto;

import com.example.tradedemo.domain.chat.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRoomResponse(
        Long id,
        String name,
        String displayName,   // 조회자 기준 상대방 닉네임으로 동적 생성
        String myRole,        // BUYER / SELLER
        Long listingId,       // 어떤 상품에 대한 채팅방인지
        String listingName,   // 상품명
        LocalDateTime createdAt
) {
    public static ChatRoomResponse of(ChatRoom room, String otherNickname, String myRole, String listingName) {

        Long listingId = room.getMarketListing() != null ? room.getMarketListing().getId() : null;
        return new ChatRoomResponse(
                room.getId(),
                room.getName(),
                otherNickname + "와의 채팅",
                myRole,
                listingId,
                listingName,
                room.getCreatedAt());
    }
}
