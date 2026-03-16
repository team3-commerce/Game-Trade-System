package com.example.tradedemo.domain.pending.repository;

import com.example.tradedemo.domain.pending.entity.PendingAssetExpiredHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingAssetExpiredHistoryRepository extends JpaRepository<PendingAssetExpiredHistory, Long> {
}