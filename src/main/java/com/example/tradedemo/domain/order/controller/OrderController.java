package com.example.tradedemo.domain.order.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.order.dto.CreateOrderResponse;
import com.example.tradedemo.domain.order.dto.GetTransactionResponse;
import com.example.tradedemo.domain.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    /**
     * 상품 구매
     */
    @PostMapping("/v1/market-listings/{marketListingId}")
    public void purchase(
            @PathVariable Long marketListingId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getMember().getId();
        orderService.purchase(memberId, marketListingId);
    }

    @PostMapping("/v2/market-listings/{marketListingId}")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> purchaseV2(
            @PathVariable Long marketListingId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getMember().getId();
        return ResponseEntity.ok(
                ApiResponse.success(String.valueOf(HttpStatus.OK), orderService.purchaseV2(memberId, marketListingId)));
    }

    /**
     * 내 구매 내역 조회
     */
    @GetMapping("/v1/me/purchases")
    public List<GetTransactionResponse> getMyBuyer(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getMember().getId();
        return orderService.getMyBuyer(memberId);
    }

    /**
     * 내 판매 내역 조회
     */
    @GetMapping("/v1/me/sales")
    public List<GetTransactionResponse> getMySeller(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getMember().getId();
        return orderService.getMySeller(memberId);
    }
}
