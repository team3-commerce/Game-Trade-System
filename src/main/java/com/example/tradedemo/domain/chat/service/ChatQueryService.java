package com.example.tradedemo.domain.chat.service;

import com.example.tradedemo.domain.chat.dto.ChatMessageResponse;
import com.example.tradedemo.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatQueryService {
    private final ChatMessageRepository repository;

    // 최근 메세지 n개 조회
    public List<ChatMessageResponse> getRecentMessages(int size) {

        Pageable pageable = PageRequest.of(0, size);

        return repository.findRecentMessages(pageable)
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

    // 커서 기반 최근 메세지 n개 조회
    // 내가 마지막으로 읽은 messageId
    public List<ChatMessageResponse> getMessagesBefore(Long lastMessageId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        return repository.findMessagesBefore(lastMessageId, pageable)
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

    // 채팅방별 메세지 조회
    public List<ChatMessageResponse> getRecentMessages(Long roomId, int size) {

        Pageable pageable = PageRequest.of(0, size);

        return repository.findRecentByRoom(roomId, pageable)
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }
}
