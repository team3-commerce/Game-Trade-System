package com.example.tradedemo.domain.coupon.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.coupon.enums.CouponHistoryStatus;
import com.example.tradedemo.domain.members.entity.Member;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupon_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponHistory extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal moneyAmount;

    @Column(nullable = false)
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponHistoryStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_coupons_id", nullable = false)
    private MemberCoupon memberCoupon;

    public static CouponHistory create(Member member, MemberCoupon memberCoupon) {
        CouponHistory history = new CouponHistory();
        history.moneyAmount = memberCoupon.getCouponPolicy().getMoneyAmount();
        history.usedAt = LocalDateTime.now();
        history.status = CouponHistoryStatus.USED;
        history.member = member;
        history.memberCoupon = memberCoupon;
        return history;
    }

    public static CouponHistory createExpired(Member member, MemberCoupon memberCoupon) {
        CouponHistory history = new CouponHistory();
        history.moneyAmount = memberCoupon.getCouponPolicy().getMoneyAmount();
        history.usedAt = LocalDateTime.now();
        history.status = CouponHistoryStatus.EXPIRED;
        history.member = member;
        history.memberCoupon = memberCoupon;
        return history;
    }
}
