package com.example.tradedemo.domain.pending.repository;

import com.example.tradedemo.domain.pending.entity.PendingAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingAssetRepository extends JpaRepository<PendingAsset, Long> {

    // 특정 회원의 미수령 자산이 존재하는지 확인
    boolean existsByMemberIdAndIsClaimedFalse(Long memberId);
}
