package com.example.tradedemo.domain.chat.repository;

import com.example.tradedemo.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
    select m
    from ChatMessage m
    join fetch m.sender
    order by m.id desc
    """)
    List<ChatMessage> findRecentMessages(Pageable pageable);

    @Query("""
    select m
    from ChatMessage m
    join fetch m.sender
    where m.id < :lastMessageId
    order by m.id desc
    """)
    List<ChatMessage> findMessagesBefore(
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable
    );

    @Query("""
    select m
    from ChatMessage m
    join fetch m.sender
    where m.chatRoom.id = :roomId
    order by m.id desc
    """)
    List<ChatMessage> findRecentByRoom(@Param("roomId") Long roomId,Pageable pageable);
}
