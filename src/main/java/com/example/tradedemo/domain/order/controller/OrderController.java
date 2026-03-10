package com.example.tradedemo.domain.order.controller;

import com.example.tradedemo.domain.order.dto.response.*;
import com.example.tradedemo.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @PathVariable Long marketListingId,
            @RequestParam Long memberId
    ){
        orderService.purchase(memberId, marketListingId);
    }

    /**
     * 내 구매 내역 조회
     */
    @GetMapping("/me/purchases")
    public List<TransactionResponse> getMyBuyer(
            @RequestParam Long memberId
    ){
        return orderService.getMyBuyer(memberId);
    }

    /**
     * 내 판매 내역 조회
     */
    @GetMapping("/me/sales")
    public List<TransactionResponse> getMySeller(
            @RequestParam Long memberId
    ){
        return orderService.getMySeller(memberId);
    }

}