package com.example.tradedemo.domain.wallet.service;

import com.example.tradedemo.domain.wallet.dto.response.WalletResponse;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    /**
     * 내 지갑 조회
     */
    @Transactional(readOnly = true)
    public WalletResponse getMyWallet(Long memberId) {

        Wallet wallet = walletRepository.findByMemberId(memberId).orElseThrow();

        return WalletResponse.of(wallet);
    }
}
