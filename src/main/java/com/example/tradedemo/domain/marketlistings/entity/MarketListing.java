package com.example.tradedemo.domain.marketlistings.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 매물 엔티티
 * 판매자가 작성한 거래 매물을 구매자가 보는 용도
 */
@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarketListing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
