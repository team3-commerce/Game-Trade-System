package com.example.tradedemo.domain.chat.controller;

import com.example.tradedemo.domain.chat.dto.ChatMessageResponse;
import com.example.tradedemo.domain.chat.service.ChatQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatQueryController {

    private final ChatQueryService chatQueryService;

    // 최근 메세지 조회 (기본값 50)
    @GetMapping("/messages")
    public List<ChatMessageResponse> getMessages(
            @RequestParam(defaultValue = "50") int size) {
        return chatQueryService.getRecentMessages(size);
    }

    // 커서 기반 최근 메세지 조회, 마지막으로 읽은 messageId 기준
    @GetMapping("/messages/before/{id}")
    public List<ChatMessageResponse> getMessagesBefore(
            @PathVariable Long id,
            @RequestParam(defaultValue = "50") int size) {
        return chatQueryService.getMessagesBefore(id, size);
    }

    // 채팅방별 메세지 조회
    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageResponse> getMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "50") int size) {
        return chatQueryService.getRecentMessages(roomId, size);
    }
}
