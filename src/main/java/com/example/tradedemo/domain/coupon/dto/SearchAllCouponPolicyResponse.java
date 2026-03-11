package com.example.tradedemo.domain.coupon.dto;

import com.example.tradedemo.domain.coupon.entity.CouponPolicy;
import com.example.tradedemo.domain.coupon.enums.IssueType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "id",
    "name",
    "moneyAmount",
    "issueType",
    "totalQuantity",
    "expendQuantity",
    "policyStartedAt",
    "policyExpiredAt",
    "policyDuration",
    "couponDuration",
    "createdAt",
    "modifiedAt"
})
public class SearchAllCouponPolicyResponse {
    private final Long id;
    private final String name;
    private final BigDecimal moneyAmount;
    private final IssueType issueType;
    private final Long totalQuantity;
    private final Long expendQuantity;
    private final LocalDateTime policyStartedAt;
    private final LocalDateTime policyExpiredAt;
    private final DurationResponse policyDuration; // 일/시간/분/초
    private final DurationResponse couponDuration; // 일/시간/분/초
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private SearchAllCouponPolicyResponse(
            Long id,
            String name,
            BigDecimal moneyAmount,
            IssueType issueType,
            Long totalQuantity,
            Long expendQuantity,
            LocalDateTime policyStartedAt,
            LocalDateTime policyExpiredAt,
            DurationResponse policyDuration,
            DurationResponse couponDuration,
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
        this.policyDuration = policyDuration;
        this.couponDuration = couponDuration;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static SearchAllCouponPolicyResponse of(CouponPolicy policy) {
        return new SearchAllCouponPolicyResponse(
                policy.getId(),
                policy.getName(),
                policy.getMoneyAmount(),
                policy.getIssueType(),
                policy.getTotalQuantity(),
                policy.getExpendQuantity(),
                policy.getPolicyStartedAt(),
                policy.getPolicyExpiredAt(),
                DurationResponse.of(policy.getPolicyDuration()),
                DurationResponse.of(policy.getCouponDuration()),
                policy.getCreatedAt(),
                policy.getModifiedAt());
    }
}
