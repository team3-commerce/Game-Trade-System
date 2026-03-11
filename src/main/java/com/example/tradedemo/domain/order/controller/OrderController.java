package com.example.tradedemo.domain.order.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.domain.order.dto.response.*;
import com.example.tradedemo.domain.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    /**
     * 상품 구매
     */
    @PostMapping("/market-listings/{marketListingId}")
    public void purchase(
            @PathVariable Long marketListingId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getMember().getId();
        orderService.purchase(memberId, marketListingId);
    }

    /**
     * 내 구매 내역 조회
     */
    @GetMapping("/me/purchases")
    public List<TransactionResponse> getMyBuyer(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getMember().getId();
        return orderService.getMyBuyer(memberId);
    }

    /**
     * 내 판매 내역 조회
     */
    @GetMapping("/me/sales")
    public List<TransactionResponse> getMySeller(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getMember().getId();
        return orderService.getMySeller(memberId);
    }
}
