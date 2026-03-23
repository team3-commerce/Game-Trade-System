package com.example.tradedemo.domain.wallet.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.entity.CouponHistory;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.order.entity.Order;
import com.example.tradedemo.domain.wallet.dto.WalletResponse;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.entity.WalletHistories;
import com.example.tradedemo.domain.wallet.enums.WalletStatus;
import com.example.tradedemo.domain.wallet.repository.WalletHistoryRepository;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletHistoryRepository walletHistoryRepository;

    @Transactional
    public void createWallet(Member member, BigDecimal initialBalance) {
        walletRepository.save(Wallet.create(member, initialBalance));
    }

    /**
     * 내 지갑 조회
     */
    @Transactional(readOnly = true)
    public WalletResponse getMyWallet(Long memberId) {

        Wallet wallet = walletRepository.findByMemberId(memberId).orElseThrow();

        return WalletResponse.of(wallet);
    }

    @Transactional(readOnly = true)
    public Wallet findWallet(Long buyerId) {
        return walletRepository.findByMemberId(buyerId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_WALLET_NOT_FOUND)
        );
    }

    @Transactional
    public void payForOrder(Wallet wallet, MarketListing marketListing, Order order) {
        wallet.decrease(marketListing.getTotalPrice());

        walletHistoryRepository.save(WalletHistories.create(
                marketListing.getTotalPrice().negate(),
                        WalletStatus.PURCHASE,
                        wallet.getBalance(),
                        wallet,
                        null,
                        wallet.getMember(),
                        order
                )
        );
    }

    @Transactional
    public void addCouponBalance(Wallet wallet, CouponHistory couponHistory, Member member) {
        wallet.addBalance(couponHistory.getMoneyAmount());

        walletHistoryRepository.save(WalletHistories.create(
                couponHistory.getMoneyAmount(),
                WalletStatus.COUPON,
                wallet.getBalance(),
                wallet,
                couponHistory,
                member,
                null));
    }
}
