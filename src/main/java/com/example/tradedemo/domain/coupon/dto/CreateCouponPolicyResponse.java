package com.example.tradedemo.domain.coupon.dto;

import com.example.tradedemo.domain.coupon.entity.CouponPolicy;
import com.example.tradedemo.domain.coupon.enums.IssueType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CreateCouponPolicyResponse {

    private final Long id;
    private final String name;
    private final BigDecimal moneyAmount;
    private final IssueType issueType;
    private final Long totalQuantity;
    private final Long expendQuantity;
    private final LocalDateTime policyStartedAt;
    private final LocalDateTime policyExpiredAt;
    private final Long policyDurationSeconds;
    private final Long couponDurationSeconds;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private CreateCouponPolicyResponse(
            Long id,
            String name,
            BigDecimal moneyAmount,
            IssueType issueType,
            Long totalQuantity,
            Long expendQuantity,
            LocalDateTime policyStartedAt,
            LocalDateTime policyExpiredAt,
            Long policyDurationSeconds,
            Long couponDurationSeconds,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt) {

        this.id = id;
        this.name = name;
        this.moneyAmount = moneyAmount;
        this.issueType = issueType;
        this.totalQuantity = totalQuantity;
        this.expendQuantity = expendQuantity;
        this.policyStartedAt = policyStartedAt;
        this.policyExpiredAt = policyExpiredAt;
        this.policyDurationSeconds = policyDurationSeconds;
        this.couponDurationSeconds = couponDurationSeconds;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static CreateCouponPolicyResponse from(CouponPolicy policy) {
        return new CreateCouponPolicyResponse(
                policy.getId(),
                policy.getName(),
                policy.getMoneyAmount(),
                policy.getIssueType(),
                policy.getTotalQuantity(),
                policy.getExpendQuantity(),
                policy.getPolicyStartedAt(),
                policy.getPolicyExpiredAt(),
                policy.getPolicyDuration() != null ? policy.getPolicyDuration().toSeconds() : null,
                policy.getCouponDuration() != null ? policy.getCouponDuration().toSeconds() : null,
                policy.getCreatedAt(),
                policy.getModifiedAt());
    }
}
