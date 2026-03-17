package com.example.tradedemo.domain.coupon.dto;

import com.example.tradedemo.domain.coupon.enums.IssueType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCouponPolicyRequest {
    @NotBlank(message = "쿠폰 정책 이름은 필수입니다")
    private String name;

    @NotNull(message = "지급 금액은 필수입니다")
    @Positive(message = "지급 금액은 0보다 커야 합니다")
    private BigDecimal moneyAmount;

    @NotNull(message = "발급 타입은 필수입니다")
    private IssueType issueType;

    // FIRST_COME 이면 필수, AUTO_SIGNUP 이면 null
    private Long totalQuantity;

    // null이면 상수 기본값 사용
    private DurationRequest policyDuration;
    private DurationRequest couponDuration;

}
