package com.example.tradedemo.domain.coupon.scheduler;

import com.example.tradedemo.domain.coupon.entity.CouponHistory;
import com.example.tradedemo.domain.coupon.entity.MemberCoupon;
import com.example.tradedemo.domain.coupon.repository.CouponHistoryRepository;
import com.example.tradedemo.domain.coupon.repository.MemberCouponRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponExpiryScheduler {

    private final MemberCouponRepository memberCouponRepository;
    private final CouponHistoryRepository couponHistoryRepository;

    /**
     * 매일 자정에 실행
     * UNUSED 상태이고 만료일이 지난 쿠폰을 EXPIRED로 변경 및 쿠폰 기록 추가
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireCoupons() {
        LocalDateTime now = LocalDateTime.now();

        List<MemberCoupon> expiredCoupons = memberCouponRepository.findAllExpiredCoupons(now);

        if (expiredCoupons.isEmpty()) {
            log.info("[만료일 지난 쿠폰 처리] 만료 처리할 쿠폰 없음");
            return;
        }

        List<CouponHistory> histories = expiredCoupons.stream()
                .peek(MemberCoupon::updateExpireStatus)
                .map(mc -> CouponHistory.createExpired(mc.getMember(), mc))
                .toList();

        couponHistoryRepository.saveAll(histories);

        log.info("[만료일 지난 쿠폰 처리] 만료 처리 완료 - {}건", histories.size());
    }
}
