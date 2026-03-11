package com.example.tradedemo.domain.wallet.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.members.entity.Member;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지갑 엔티티
 * 현재 내가 보유하고 있는 돈
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wallets")
public class Wallet extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 지갑 잔액
     */
    @Column(nullable = false)
    private BigDecimal balance;

    /**
     * 사용자 ID
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static Wallet create(Member member, BigDecimal balance) {
        Wallet wallet = new Wallet();
        wallet.balance = balance;
        wallet.member = member;

        return wallet;
    }

    public void updateBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
