package com.example.tradedemo.domain.chat.controller;

import com.example.tradedemo.domain.chat.entity.ChatRoom;
import com.example.tradedemo.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    // 채팅방 생성
    @PostMapping
    public ChatRoom create(@RequestParam String name) {
        ChatRoom room = new ChatRoom(name);
        return chatRoomRepository.save(room);
    }
}
