package com.example.tradedemo.domain.coupon.dto;

import com.example.tradedemo.domain.coupon.entity.CouponHistory;
import com.example.tradedemo.domain.coupon.enums.CouponHistoryStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"couponHistoryId", "couponName", "moneyAmount", "status", "usedAt", "createdAt", "modifiedAt"})
public class SearchAllCouponHistoryResponse {

    private final Long couponHistoryId;
    private final String couponName;
    private final BigDecimal moneyAmount;
    private final CouponHistoryStatus status;
    private final LocalDateTime usedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private SearchAllCouponHistoryResponse(
            Long couponHistoryId,
            String couponName,
            BigDecimal moneyAmount,
            CouponHistoryStatus status,
            LocalDateTime usedAt,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt) {
        this.couponHistoryId = couponHistoryId;
        this.couponName = couponName;
        this.moneyAmount = moneyAmount;
        this.status = status;
        this.usedAt = usedAt;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static SearchAllCouponHistoryResponse of(CouponHistory couponHistory) {
        return new SearchAllCouponHistoryResponse(
                couponHistory.getId(),
                couponHistory.getMemberCoupon().getCouponPolicy().getName(),
                couponHistory.getMoneyAmount(),
                couponHistory.getStatus(),
                couponHistory.getUsedAt(),
                couponHistory.getCreatedAt(),
                couponHistory.getModifiedAt());
    }
}
