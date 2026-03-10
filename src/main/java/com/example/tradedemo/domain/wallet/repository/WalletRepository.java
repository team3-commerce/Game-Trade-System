package com.example.tradedemo.domain.wallet.repository;

import com.example.tradedemo.domain.wallet.entity.Wallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    // 내 지갑 확인(돈)
    Optional<Wallet> findByMemberId(Long memberId);
}
