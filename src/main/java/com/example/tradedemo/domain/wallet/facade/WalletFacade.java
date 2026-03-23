package com.example.tradedemo.domain.wallet.facade;

import com.example.tradedemo.domain.coupon.entity.CouponHistory;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.order.entity.Order;
import com.example.tradedemo.domain.wallet.dto.WalletResponse;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.entity.WalletHistories;
import com.example.tradedemo.domain.wallet.enums.WalletStatus;
import com.example.tradedemo.domain.wallet.service.WalletService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class WalletFacade {

    private final WalletService walletService;

    @Transactional
    public void createWallet(Member member, BigDecimal initialBalance) {
        walletService.createWallet(member, initialBalance);
    }

    /**
     * 내 지갑 조회
     */
    @Transactional(readOnly = true)
    public WalletResponse getMyWallet(Long memberId) {
        Wallet wallet = walletService.findWallet(memberId);
        return WalletResponse.of(wallet);
    }

    @Transactional
    public void payForOrder(Wallet wallet, MarketListing marketListing, Order order) {
        wallet.decrease(marketListing.getTotalPrice());

        walletService.saveHistory(WalletHistories.create(
                marketListing.getTotalPrice().negate(),
                WalletStatus.PURCHASE,
                wallet.getBalance(),
                wallet,
                null,
                wallet.getMember(),
                order
        ));
    }

    @Transactional
    public void addCouponBalance(Wallet wallet, CouponHistory couponHistory, Member member) {
        wallet.addBalance(couponHistory.getMoneyAmount());

        walletService.saveHistory(WalletHistories.create(
                couponHistory.getMoneyAmount(),
                WalletStatus.COUPON,
                wallet.getBalance(),
                wallet,
                couponHistory,
                member,
                null));
    }

    @Transactional
    public void addBalanceWithHistory(Wallet wallet, BigDecimal amount, Order order) {
        wallet.addBalance(amount);

        walletService.saveHistory(WalletHistories.create(
                amount,
                WalletStatus.PURCHASE,
                wallet.getBalance(),
                wallet,
                null,
                wallet.getMember(),
                order
        ));
    }
}
