package com.example.tradedemo.domain.wallet.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.wallet.dto.WalletResponse;
import com.example.tradedemo.domain.wallet.facade.WalletFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WalletController {

    private final WalletFacade walletFacade;

    @GetMapping("/api/v1/wallets/me")
    public ResponseEntity<ApiResponse<WalletResponse>> getMyWallet(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        WalletResponse res = walletFacade.getMyWallet(principalDetails.getMember().getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }
}
