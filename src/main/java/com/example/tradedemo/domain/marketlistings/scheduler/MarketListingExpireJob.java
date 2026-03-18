package com.example.tradedemo.domain.marketlistings.scheduler;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.scheduler.BaseExpireJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketListingExpireJob extends BaseExpireJob {

    private final MarketListingRepository marketListingRepository;

    @Override
    protected void executeExpireLogic() {
        List<MarketListing> expiredListings =
                marketListingRepository.findByStatusAndSaleEndAtBefore(MarketListingStatus.SELLING, LocalDateTime.now());

        if (expiredListings.isEmpty()) {
            log.info("[MarketListing Quartz] 만료 상품 없음");
            return;
        }

        for (MarketListing listing : expiredListings) {
            listing.updateStatus(MarketListingStatus.EXPIRED);
        }

        log.info("[MarketListing Quartz] 만료 처리 완료 count={}", expiredListings.size());
    }
}