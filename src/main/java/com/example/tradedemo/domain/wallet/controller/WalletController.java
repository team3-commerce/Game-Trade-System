package com.example.tradedemo.domain.wallet.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.domain.wallet.dto.response.WalletResponse;
import com.example.tradedemo.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/me")
public class WalletController {

    private final WalletService walletService;

    /**
     * 내 지갑 조회
     */
    @GetMapping("/wallet")
    public WalletResponse getMyWallet(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getMember().getId();
        return walletService.getMyWallet(memberId);
    }
}
