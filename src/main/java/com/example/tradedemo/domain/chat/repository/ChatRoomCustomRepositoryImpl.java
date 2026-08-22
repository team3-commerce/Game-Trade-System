package com.example.tradedemo.domain.chat.repository;

import com.example.tradedemo.domain.chat.entity.ChatRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.example.tradedemo.domain.chat.entity.QChatRoom.chatRoom;

@RequiredArgsConstructor
public class ChatRoomCustomRepositoryImpl implements ChatRoomCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ChatRoom> findByIdWithListing(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(chatRoom)
                        .leftJoin(chatRoom.marketListing).fetchJoin()
                        .where(chatRoom.id.eq(id))
                        .fetchOne()
        );
    }
}
