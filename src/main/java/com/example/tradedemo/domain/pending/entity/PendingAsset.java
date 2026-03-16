package com.example.tradedemo.domain.pending.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.order.entity.Order;
import com.example.tradedemo.domain.pending.enums.PendingType;
import com.example.tradedemo.domain.pending.enums.Type;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 수령 대기 엔티티
 * 구매자와 판매자가 수령할 돈 또는 아이템
 */
@Entity
@Getter
@Table(name = "pending_asset")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PendingAsset extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 수령 대기 타입
     */
    @Enumerated(EnumType.STRING)
    private PendingType pendingType; // 기본 : SALE_SUCCESS 판매중
    /**
     * 대기중인 물품의 타입
     * 돈이냐, 아이템이냐
     */
    @Enumerated(EnumType.STRING)
    private Type type; // 기본 : ITEM 아이템

    /**
     * 수령할 돈
     */
    @Column(nullable = false)
    private BigDecimal moneyAmount;
    /**
     * 수령할 아이템 수량
     */
    @Column(nullable = false)
    private Long itemQuantity;
    /**
     * 아이템 수령 여부
     */
    @Column(nullable = false)
    private Boolean isClaimed;
    /**
     * 수령 시간, null일 수도 있습니다
     */
    @Column(nullable = true)
    private LocalDateTime claimedAt;

    /**
     * 수령 만료 시간
     */
    @Column(nullable = false)
    private LocalDateTime expiredAt;

    /**
     * 거래 매물 ID
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "market_listings_id", nullable = false)
    private MarketListing marketListing;


    /**
     * 주문 ID
     * 주문 ID, null일 수도 있습니다
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "order_id", nullable = true)
    private Order order;

    /**
     * 사용자 ID
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static PendingAsset create(
            PendingType pendingType,
            Type type,
            BigDecimal moneyAmount,
            Long itemQuantity,
            Boolean isClaimed,
            LocalDateTime claimedAt,
            LocalDateTime expiredAt,
            MarketListing marketListing,
            Order order,
            Member member) {
        PendingAsset asset = new PendingAsset();
        asset.pendingType = pendingType;
        asset.type = type;
        asset.moneyAmount = moneyAmount;
        asset.itemQuantity = itemQuantity;
        asset.isClaimed = isClaimed;
        asset.claimedAt = claimedAt;
        asset.expiredAt = expiredAt;
        asset.marketListing = marketListing;
        asset.order = order;
        asset.member = member;

        return asset;
    }
    /**
     * 수령 여부
     * @param claimed
     */
    public void setClaimed(Boolean claimed) {
        this.isClaimed = claimed;
    }
    /**
     * 수령시간
     * @param claimedAt
     */
    public void setClaimedAt(LocalDateTime claimedAt) {
        this.claimedAt = claimedAt;
    }
    /**
     * 만료타입
     */
    public void setExpireType() {
        this.pendingType = PendingType.EXPIRED;
    }
}
