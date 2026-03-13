package com.example.tradedemo.domain.wallet.dto;

import com.example.tradedemo.domain.wallet.entity.Wallet;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class WalletResponse {

    private final Long walletId;
    private final BigDecimal balance;
    private final Long memberId;

    private WalletResponse(Long walletId, BigDecimal balance, Long memberId) {
        this.walletId = walletId;
        this.balance = balance;
        this.memberId = memberId;
    }

    public static WalletResponse of(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(), wallet.getBalance(), wallet.getMember().getId());
    }
}
