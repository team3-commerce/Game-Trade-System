package com.example.tradedemo.domain.chat.repository;

import com.example.tradedemo.domain.chat.dto.ChatRoomResponse;
import com.example.tradedemo.domain.chat.entity.ChatRoom;
import com.example.tradedemo.domain.chat.enums.ChatRoomMemberRole;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberCustomRepository {

    // 내가 참여한 전체 채팅방 목록 (상대방 닉네임 + 내 역할 + 상품명 포함)
    List<ChatRoomResponse> findChatRoomsByMemberEmail(String email);

    // 역할별 채팅방 목록
    List<ChatRoomResponse> findChatRoomsByMemberEmailAndRole(String email, ChatRoomMemberRole role);

    // 같은 구매자 + 같은 상품 채팅방 중복 체크
    Optional<ChatRoom> findRoomByBuyerAndListing(String buyerEmail, Long listingId);
}
