package com.example.tradedemo.domain.coupon.dto;

import com.example.tradedemo.common.dto.DurationResponse;
import com.example.tradedemo.domain.coupon.entity.MemberCoupon;
import com.example.tradedemo.domain.coupon.enums.CouponStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "memberCouponId",
    "couponName",
    "moneyAmount",
    "status",
    "couponDuration",
    "issuedAt",
    "expiredAt",
    "createdAt",
    "modifiedAt"
})
public class SearchAllMemberCouponResponse {

    private final Long memberCouponId;
    private final String couponName;
    private final BigDecimal moneyAmount;
    private final CouponStatus status;
    private final DurationResponse couponDuration;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiredAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private SearchAllMemberCouponResponse(
            Long memberCouponId,
            String couponName,
            BigDecimal moneyAmount,
            CouponStatus status,
            DurationResponse couponDuration,
            LocalDateTime issuedAt,
            LocalDateTime expiredAt,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt) {
        this.memberCouponId = memberCouponId;
        this.couponName = couponName;
        this.moneyAmount = moneyAmount;
        this.status = status;
        this.couponDuration = couponDuration;
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static SearchAllMemberCouponResponse of(MemberCoupon memberCoupon) {
        return new SearchAllMemberCouponResponse(
                memberCoupon.getId(),
                memberCoupon.getCouponPolicy().getName(),
                memberCoupon.getCouponPolicy().getMoneyAmount(),
                memberCoupon.getStatus(),
                DurationResponse.of(memberCoupon.getCouponPolicy().getCouponDuration()),
                memberCoupon.getIssuedAt(),
                memberCoupon.getExpiredAt(),
                memberCoupon.getCreatedAt(),
                memberCoupon.getModifiedAt());
    }
}
