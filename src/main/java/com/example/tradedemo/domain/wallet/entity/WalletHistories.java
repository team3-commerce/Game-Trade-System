package com.example.tradedemo.domain.wallet.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.coupon.entity.CouponHistory;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.order.entity.Order;
import com.example.tradedemo.domain.wallet.enums.WalletStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지갑 이력 엔티티
 * 지갑의 변동액 기록
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wallet_histories")
public class WalletHistories extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 금액 변동
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * 변동 타입
     */
    @Enumerated(EnumType.STRING)
    private WalletStatus type;

    /**
     * 변동 후 잔액
     */
    private BigDecimal balanceSnapshot;

    /**
     * 지갑 ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    /**
     * 쿠폰 기록 ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_history_id")
    private CouponHistory couponHistory;

    /**
     * 주문 테이블 ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * 지갑 테이블 ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static WalletHistories create(
            BigDecimal amount,
            WalletStatus type,
            BigDecimal balanceSnapshot,
            Wallet wallet,
            CouponHistory couponHistory,
            Member member,
            Order order) {
        WalletHistories history = new WalletHistories();
        history.amount = amount;
        history.type = type;
        history.balanceSnapshot = balanceSnapshot;
        history.wallet = wallet;
        history.couponHistory = couponHistory;
        history.member = member;
        history.order = order;

        return history;
    }
}
