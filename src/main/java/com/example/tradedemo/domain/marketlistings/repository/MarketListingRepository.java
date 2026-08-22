package com.example.tradedemo.domain.marketlistings.repository;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
    import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
    import org.springframework.data.jpa.repository.JpaRepository;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;
public interface MarketListingRepository extends JpaRepository<MarketListing, Long>, MarketListingCustomRepository {

    // 특정 회원의 특정 상태 매물이 존재하는지 확인
    boolean existsByMemberIdAndStatus(Long memberId, MarketListingStatus status);

    /**
     * 일정 시간이 지나면 SELLING(파는 중) → EXPIRED(만료)
     */
    List<MarketListing> findByStatusAndSaleEndAtBefore(MarketListingStatus status, LocalDateTime now);
}
