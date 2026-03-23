package com.example.tradedemo.domain.chat.repository;

import com.example.tradedemo.domain.chat.dto.ChatMessageResponse;

import java.util.List;

public interface ChatMessageCustomRepository {

    /**
     * 채팅방별 커서 기반 메시지 조회
     *
     * lastMessageId == null → 최신 n개 (최초 입장 / 재연결 복구)
     * lastMessageId != null → 해당 ID 이전 n개 (이전 메시지 더 보기)
     */
    List<ChatMessageResponse> findByRoomWithCursor(Long roomId, Long lastMessageId, int size);
}
