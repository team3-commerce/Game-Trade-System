package com.example.tradedemo.domain.coupon.dto;

import com.example.tradedemo.domain.coupon.enums.IssueType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class CreateCouponPolicyRequest {
    @NotBlank(message = "쿠폰 정책 이름은 필수입니다")
    private final String name;

    @NotNull(message = "지급 금액은 필수입니다")
    @Positive(message = "지급 금액은 0보다 커야 합니다")
    private final BigDecimal moneyAmount;

    @NotNull(message = "발급 타입은 필수입니다")
    private final IssueType issueType;

    // FIRST_COME 이면 필수, AUTO_SIGNUP 이면 null
    private final Long totalQuantity;

    @JsonCreator
    private CreateCouponPolicyRequest(
            @JsonProperty("name") String name,
            @JsonProperty("moneyAmount") BigDecimal moneyAmount,
            @JsonProperty("issueType") IssueType issueType,
            @JsonProperty("totalQuantity") Long totalQuantity) {
        this.name = name;
        this.moneyAmount = moneyAmount;
        this.issueType = issueType;
        this.totalQuantity = totalQuantity;
    }

    public static CreateCouponPolicyRequest of(
            String name, BigDecimal moneyAmount, IssueType issueType, Long totalQuantity) {
        return new CreateCouponPolicyRequest(name, moneyAmount, issueType, totalQuantity);
    }
}
