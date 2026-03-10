package com.example.tradedemo.domain.marketlistings.controller;

import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.marketlistings.dto.request.CreateMarketListingRequest;
import com.example.tradedemo.domain.marketlistings.dto.response.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.response.SearchMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.service.MarketListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MarketListingController {
    private final MarketListingService marketListingService;

    /**
     * 상품 등록
     */
    @PostMapping("/api/v1/market-listings")
    public ResponseEntity<ApiResponse<Long>> createMarketListing(
            @RequestParam Long memberId,
            @RequestBody CreateMarketListingRequest request) {

        Long listingId = marketListingService.create(memberId, request);

        return ResponseEntity.ok(
                ApiResponse.success(String.valueOf(HttpStatus.OK.value()), listingId)
        );
    }
    /**
     *  마켓 상품 전체 조회
     *  sortTotalPrice, sortSaleEndAt 값을 asc/desc 로 전달하여 정렬 조건 추가 가능
     *  asc/desc 가 아닌 값 전달 시 정렬 조건에서 무시 (예외 처리 x)
     */
    @GetMapping("/api/v1/market-listings")
    public ResponseEntity<ApiResponse<Page<SearchAllMarketListingResponse>>> getAllMarketListing(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortTotalPrice,
            @RequestParam(required = false) String sortSaleEndAt,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10);

        return ResponseEntity.ok(ApiResponse.success(
                String.valueOf(HttpStatus.OK.value()),
                marketListingService.getAllMarketListing(keyword, sortTotalPrice, sortSaleEndAt, pageable)));
    }

    /**
     * 마켓 상품 단건 조회
     */
    @GetMapping("/api/v1/market-listings/{marketListingId}")
    public ResponseEntity<ApiResponse<SearchMarketListingResponse>> getMarketListing(
            @PathVariable Long marketListingId) {
        return ResponseEntity.ok(ApiResponse.success(
                String.valueOf(HttpStatus.OK), marketListingService.getMarketListing(marketListingId)));
    }
}
