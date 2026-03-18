package com.example.tradedemo.domain.marketlistings.scheduler;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketListingsScheduler {

    private final MarketListingRepository marketListingRepository;

    /**
     * 1분마다 실행
     * 판매 만료된 상품 상태 변경 : SELLING → EXPIRED
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expireMarketListings() {

        List<MarketListing> expiredListings =
                marketListingRepository.findByStatusAndSaleEndAtBefore(
                        MarketListingStatus.SELLING,
                        LocalDateTime.now()
                );

        if (expiredListings.isEmpty()) {
            log.info("[MarketListings Scheduler] 만료 상품 없음");
            return;
        }

        for (MarketListing listing : expiredListings) {
            listing.updateStatus(MarketListingStatus.EXPIRED);
        }

        log.info("[MarketListings Scheduler] 만료 처리 완료 count={}", expiredListings.size());
    }
}