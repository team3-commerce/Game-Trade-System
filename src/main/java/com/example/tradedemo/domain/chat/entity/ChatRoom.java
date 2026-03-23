package com.example.tradedemo.domain.chat.entity;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_listing_id")
    private MarketListing marketListing;

    private LocalDateTime createdAt;

    public static ChatRoom create(String name, MarketListing marketListing) {
        ChatRoom room = new ChatRoom();
        room.name = name;
        room.marketListing = marketListing;
        room.createdAt = LocalDateTime.now();
        return room;
    }
}
