package com.example.tradedemo.domain.chat.repository;

import com.example.tradedemo.domain.chat.dto.ChatMessageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.tradedemo.domain.chat.entity.QChatMessage.chatMessage;

@RequiredArgsConstructor
public class ChatMessageCustomRepositoryImpl implements ChatMessageCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatMessageResponse> findByRoomWithCursor(Long roomId, Long lastMessageId, int size) {
        List<ChatMessageResponse> messages = queryFactory
                .select(Projections.constructor(ChatMessageResponse.class,
                        chatMessage.id,
                        chatMessage.content,
                        chatMessage.sender.id,
                        chatMessage.sender.nickname,
                        chatMessage.createdAt
                ))
                .from(chatMessage)
                .join(chatMessage.sender)
                .where(
                        chatMessage.chatRoom.id.eq(roomId),
                        lastMessageId != null ? chatMessage.id.lt(lastMessageId) : null
                )
                .orderBy(chatMessage.id.desc())  // 최신순으로 n개 slice
                .limit(size)
                .fetch();

        // DESC로 가져온 결과를 오름차순(오래된 것 -> 최신)으로 반환
        return messages.stream()
                .sorted((a, b) -> Long.compare(a.getMessageId(), b.getMessageId()))
                .toList();
    }
}
