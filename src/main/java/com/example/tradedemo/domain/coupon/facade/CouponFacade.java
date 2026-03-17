package com.example.tradedemo.domain.coupon.facade;

import com.example.tradedemo.domain.coupon.entity.CouponHistory;
import com.example.tradedemo.domain.coupon.exception.CouponExpiredException;
import com.example.tradedemo.domain.coupon.service.CouponService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;
    private final WalletService walletService;

    @Transactional(noRollbackFor = CouponExpiredException.class)
    public void useCoupon(Long memberId, Long memberCouponId, Member member) {
        // 쿠폰 사용 처리
        CouponHistory couponHistory = couponService.useCoupon(memberId, memberCouponId, member);

        // 지갑 조회
        Wallet wallet = walletService.findWallet(memberId);

        // 지갑 잔액 추가 + 지갑 히스토리 저장
        walletService.addCouponBalance(wallet, couponHistory, member);
    }

    @Transactional(noRollbackFor = CouponExpiredException.class)
    @Caching(evict = {
            @CacheEvict(
                    value = "memberCoupons",
                    key = "'member:' + #memberId + ':coupon:' + #memberCouponId"),
            @CacheEvict(value = "memberCoupons",   allEntries = true),
            @CacheEvict(value = "couponHistories", allEntries = true)
    })
    public void useCouponV2(Long memberId, Long memberCouponId, Member member) {
        // 쿠폰 사용 처리
        CouponHistory couponHistory = couponService.useCoupon(memberId, memberCouponId, member);

        // 지갑 조회
        Wallet wallet = walletService.findWallet(memberId);

        // 지갑 잔액 추가 + 지갑 히스토리 저장
        walletService.addCouponBalance(wallet, couponHistory, member);
    }
}
