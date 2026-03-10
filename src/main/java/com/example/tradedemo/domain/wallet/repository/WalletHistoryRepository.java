package com.example.tradedemo.domain.wallet.repository;

import com.example.tradedemo.domain.wallet.entity.WalletHistories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletHistoryRepository extends JpaRepository<WalletHistories, Long> {}
