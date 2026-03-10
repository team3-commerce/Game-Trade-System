package com.example.tradedemo.domain.marketlistings.controller;

import com.example.tradedemo.domain.marketlistings.dto.response.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.service.MarketListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MarketListingController {
    private final MarketListingService marketListingService;

    /**
     *  마켓 상품 전체 조회
     */
    @GetMapping("/api/v1/market-listings")
    public ResponseEntity<Page<SearchAllMarketListingResponse>> getAllMarketListing(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(marketListingService.getAllMarketListing(keyword));
    }
}
