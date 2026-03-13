package com.example.tradedemo.domain.pending.repository;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.pending.entity.PendingAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PendingAssetRepository extends JpaRepository<PendingAsset, Long> {

    // 특정 회원의 미수령 자산이 존재하는지 확인
    boolean existsByMemberIdAndIsClaimedFalse(Long memberId);
    /**
     * 해당 사용자가 미수령한 수령 대기 테이블 조회
     * @param memberId
     * @return
     */
    List<PendingAsset> findByMemberIdAndIsClaimedFalse(Long memberId);

    /**
     * 개별 수령 조회
     * @param pendingAssetId
     * @param memberId
     * @return
     */
    Optional<PendingAsset> findByIdAndMemberId(Long pendingAssetId, Long memberId);
}
