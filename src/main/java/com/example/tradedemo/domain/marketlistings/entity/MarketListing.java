package com.example.tradedemo.domain.marketlistings.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 매물 엔티티
 * 판매자가 작성한 거래 매물을 구매자가 보는 용도
 */
@Entity
@Getter
@Table(name = "market_listings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarketListing extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "totla_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Long quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketListingStatus status;

    @Column(name = "sale_end_at", nullable = false)
    private LocalDateTime saleEndAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_items_id", nullable = false)
    private MemberItem memberItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static MarketListing create(
            String itemName,
            BigDecimal totalPrice,
            BigDecimal unitPrice,
            Long quantity,
            Duration saleDuration,
            MemberItem memberItem,
            Member member) {
        MarketListing marketListing = new MarketListing();

        marketListing.itemName = itemName;
        marketListing.totalPrice = totalPrice;
        marketListing.unitPrice = unitPrice;
        marketListing.quantity = quantity;
        marketListing.status = MarketListingStatus.SELLING;
        marketListing.saleEndAt = LocalDateTime.now().plus(saleDuration);
        marketListing.memberItem = memberItem;
        marketListing.member = member;

        return marketListing;
    }
}
