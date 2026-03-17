package com.example.tradedemo.domain.marketlistings.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
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
    /**
     * 거래 매물 id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 아이템 이름
     */
    @Column(length = 64, name = "item_name", nullable = false)
    private String itemName;
    /**
     * 총 가격
     */
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
    /**
     * 개당 가격
     */
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
    /**
     * 아이템 개수
     */
    @Column(nullable = false)
    private Long quantity;
    /**
     * 거래 매물 상태 : 파는 중, 팔림, CLAIMED?, 취소됨, 만료됨
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketListingStatus status;
    /**
     * 상품 등록 만료일
     */
    @Column(name = "sale_end_at", nullable = false)
    private LocalDateTime saleEndAt;
    /**
     * 판매자 아이템의 ID
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_items_id", nullable = false)
    private MemberItem memberItem;
    /**
     * 판매자 ID
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    /**
     * 아이템 이름, 총 가격, 낱개 가격, 수량, 상품 만료일, 사용자아이템 ID, 판매자 ID
     */
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

    /**
     * 상태를 업데이트 합니다.
     * @param newStatus 새 상태
     */
    public void updateStatus(MarketListingStatus newStatus) {
        this.status = newStatus;
    }

    public void validateSelling(){
        if (this.getStatus() != MarketListingStatus.SELLING) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_SELLING);
        }
    }
}
