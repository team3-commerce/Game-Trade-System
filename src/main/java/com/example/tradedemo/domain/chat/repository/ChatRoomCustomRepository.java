package com.example.tradedemo.domain.chat.repository;

import com.example.tradedemo.domain.chat.entity.ChatRoom;

import java.util.Optional;

public interface ChatRoomCustomRepository {
    Optional<ChatRoom> findByIdWithListing(Long id);
}
