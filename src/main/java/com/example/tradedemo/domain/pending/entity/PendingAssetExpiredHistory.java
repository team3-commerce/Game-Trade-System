package com.example.tradedemo.domain.pending.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.pending.enums.PendingType;
import com.example.tradedemo.domain.pending.enums.Type;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "pending_asset_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PendingAssetExpiredHistory extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 원래 PendingAsset ID
     */
    @Column(nullable = false)
    private Long pendingAssetId;

    /**
     * 사용자 ID
     */
    @Column(nullable = false)
    private Long memberId;

    /**
     * 수령 대기 타입
     * 구매/판매 → 만료
     */
    @Enumerated(EnumType.STRING)
    private PendingType pendingType;

    /**
     * 수령 종류
     */
    @Enumerated(EnumType.STRING)
    private Type type;

    /**
     * 금액
     */
    private BigDecimal moneyAmount;

    /**
     * 아이템 수량
     */
    private Long itemQuantity;

    /**
     * 주문 ID
     */
    private Long orderId;

    /**
     * 매물 ID
     */
    private Long marketListingId;

    /**
     * 만료 시간
     */
    private LocalDateTime expiredAt;

    /**
     * 삭제 시간
     */
    private LocalDateTime deletedAt;

    public static PendingAssetExpiredHistory create(PendingAsset asset) {

        PendingAssetExpiredHistory history = new PendingAssetExpiredHistory();

        history.pendingAssetId = asset.getId();
        history.memberId = asset.getMember().getId();
        history.pendingType = PendingType.EXPIRED;
        history.type = asset.getType();
        history.moneyAmount = asset.getMoneyAmount();
        history.itemQuantity = asset.getItemQuantity();
        history.expiredAt = asset.getExpiredAt();
        history.deletedAt = LocalDateTime.now();

        if (asset.getOrder() != null) {
            history.orderId = asset.getOrder().getId();
        }

        if (asset.getMarketListing() != null) {
            history.marketListingId = asset.getMarketListing().getId();
        }

        return history;
    }
}