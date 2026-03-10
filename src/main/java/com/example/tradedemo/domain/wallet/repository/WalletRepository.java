package com.example.tradedemo.domain.wallet.repository;

import com.example.tradedemo.domain.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
    // 내 지갑 확인(돈)
    Optional<Wallet> findByMemberId(Long memberId);
}
