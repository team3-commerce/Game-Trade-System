package com.example.tradedemo.domain.pending.repository;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.pending.entity.PendingAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingAssetRepository extends JpaRepository<PendingAsset, Long> {

    // 특정 회원의 미수령 자산이 존재하는지 확인
    boolean existsByMemberIdAndIsClaimedFalse(Long memberId);
    /**
     * 거래소 수령
     */
    List<PendingAsset> findByMarketListingAndMemberAndClaimedFalse(MarketListing marketListing, Member member);}
