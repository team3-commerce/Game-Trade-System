package com.example.tradedemo.domain.coupon.entity;

import com.example.tradedemo.common.converter.DurationConverter;
import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.coupon.enums.IssueType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupon_policies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponPolicy extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal moneyAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType issueType;

    // AUTO_SIGNUP 이면 null (무제한)
    @Column
    private Long totalQuantity;

    @Column(nullable = false)
    private Long expendQuantity;

    // 정책 시작일
    @Column(nullable = false)
    private LocalDateTime policyStartedAt;

    // 정책 만료일
    @Column
    private LocalDateTime policyExpiredAt;

    // 정책 유효기간 Duration → DB에 초 단위로 저장
    @Convert(converter = DurationConverter.class)
    @Column
    private Duration policyDuration;

    // 쿠폰 유효기간
    @Convert(converter = DurationConverter.class)
    @Column
    private Duration couponDuration;

    public static CouponPolicy create(
            String name,
            BigDecimal moneyAmount,
            IssueType issueType,
            Long totalQuantity,
            LocalDateTime policyStartedAt,
            LocalDateTime policyExpiredAt,
            Duration policyDuration,
            Duration couponDuration) {
        CouponPolicy policy = new CouponPolicy();
        policy.name = name;
        policy.moneyAmount = moneyAmount;
        policy.issueType = issueType;
        policy.totalQuantity = totalQuantity;
        policy.expendQuantity = 0L;
        policy.policyStartedAt = policyStartedAt;
        policy.policyExpiredAt = policyExpiredAt;
        policy.policyDuration = policyDuration;
        policy.couponDuration = couponDuration;
        return policy;
    }
}
