package com.example.tradedemo.domain.pending.repository;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.pending.entity.PendingAsset;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
     * 수령대기테이블 만료 대상
     * @param now
     * @return
     */
    List<PendingAsset> findByExpiredAtBeforeAndIsClaimedFalse(LocalDateTime now);

    /**
     * 비관적 락(개별수령조회에 락 걸었음) : V1에서 사용함
     * @param pendingAssetId
     * @param memberId
     * @return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pa " +
            "FROM PendingAsset pa " +
            "WHERE pa.id = :pendingAssetId AND pa.member.id = :memberId")
    Optional<PendingAsset> findByIdAndMemberIdWithLock(
        @Param("pendingAssetId") Long pendingAssetId,
        @Param("memberId") Long memberId
    );

    /**
     * 수령 개별조회
     * @param pendingAssetId
     * @param memberId
     * @return
     */
    Optional<PendingAsset> findByIdAndMemberId( Long pendingAssetId, Long memberId);
}
