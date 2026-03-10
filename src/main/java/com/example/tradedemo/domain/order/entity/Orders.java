package com.example.tradedemo.domain.order.entity;

import com.example.tradedemo.common.entity.BaseEntity;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.members.entity.Members;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 엔티티
 * 구매자와 판매자 간의 주문(거래) 기록을 관리한다.
 */
@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 거래 금액
     */
    @Column(nullable = false)
    private BigDecimal transactionMoney;

    /**
     * 거래 수량
     */
    @Column(nullable = false)
    private Long transactionStock;

    /**
     * 판매자 ID : seller_id
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private Members seller;

    /**
     * 구매자 ID : buyer_id
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Members buyer;

    /**
     * 거래 매물 : market_listings_id
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "market_listing_id", nullable = false)
    private MarketListing marketListing;

    /**
     * 거래 매물 ID : item_id
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "market_listing_id", nullable = false)
    private MarketListing itemId;

    /**
     * 정적 팩토리 메서드
     */
    public static Orders create(
            BigDecimal transactionMoney,
            Long transactionStock,
            Members seller,
            Members buyer,
            MarketListing marketListing,
            MarketListing itemId) {
        Orders orders = new Orders();
        orders.transactionMoney = transactionMoney;
        orders.transactionStock = transactionStock;
        orders.seller = seller;
        orders.buyer = buyer;
        orders.marketListing = marketListing;
        orders.itemId = itemId;

        return orders;
    }
}
