package com.example.tradedemo.domain.coupon.entity;

import com.example.tradedemo.common.entity.Base;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    // 정책 유효기간
    @Column
    private Integer policyDuration;

    // 쿠폰 유효기간
    @Column
    private Integer couponDuration;

}
