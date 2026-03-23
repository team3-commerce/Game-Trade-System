package com.example.tradedemo.domain.wallet.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.entity.WalletHistories;
import com.example.tradedemo.domain.wallet.repository.WalletHistoryRepository;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletHistoryRepository walletHistoryRepository;

    @Transactional
    public void createWallet(Member member, BigDecimal initialBalance) {
        walletRepository.save(Wallet.create(member, initialBalance));
    }

    @Transactional(readOnly = true)
    public Wallet findWallet(Long memberId) {
        return walletRepository.findByMemberId(memberId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_WALLET_NOT_FOUND)
        );
    }

    @Transactional
    public void saveHistory(WalletHistories history) {
        walletHistoryRepository.save(history);
    }
}
