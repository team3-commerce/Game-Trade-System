package com.example.tradedemo.domain.coupon.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.members.entity.Member;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member_coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCoupon extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_policy_id", nullable = false)
    private CouponPolicy couponPolicy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    // 회원가입 쿠폰이면 null
    @Column
    private LocalDateTime expiredAt;

    public static MemberCoupon create(
            Member member, CouponPolicy couponPolicy, LocalDateTime issuedAt, LocalDateTime expiredAt) {
        MemberCoupon memberCoupon = new MemberCoupon();
        memberCoupon.member = member;
        memberCoupon.couponPolicy = couponPolicy;
        memberCoupon.status = CouponStatus.UNUSED;
        memberCoupon.issuedAt = issuedAt;
        memberCoupon.expiredAt = expiredAt;
        return memberCoupon;
    }
}
