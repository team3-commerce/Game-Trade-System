package com.example.tradedemo.domain.marketlistings.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.marketlistings.dto.CreateMarketListingRequest;
import com.example.tradedemo.domain.marketlistings.dto.GetMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.SearchMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.SearchTrendingKeywordResponse;
import com.example.tradedemo.domain.marketlistings.service.MarketListingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MarketListingController {
    private final MarketListingService marketListingService;


    /**
     * 상품 등록 V1
     * @param details
     * @param request
     * @return
     */
    @PostMapping("/api/v1/market-listings")
    public ResponseEntity<ApiResponse<GetMarketListingResponse>> createMarketListing(
            @AuthenticationPrincipal PrincipalDetails details, @RequestBody CreateMarketListingRequest request) {
        GetMarketListingResponse res =
                marketListingService.create(details.getMember().getId(), request);

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }
    /**
     * 상품 등록 V2
     * @param details
     * @param request
     * @return
     */
    @PostMapping("/api/v2/market-listings")
    public ResponseEntity<ApiResponse<GetMarketListingResponse>> createMarketListingV2(
            @AuthenticationPrincipal PrincipalDetails details, @RequestBody CreateMarketListingRequest request) {
        GetMarketListingResponse res =
                marketListingService.createV2(details.getMember().getId(), request);

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }
    /**
     * 상품 등록 V3
     * @param details
     * @param request
     * @return
     */
    @PostMapping("/api/v3/market-listings")
    public ResponseEntity<ApiResponse<GetMarketListingResponse>> createMarketListingV3(
            @AuthenticationPrincipal PrincipalDetails details, @RequestBody CreateMarketListingRequest request) {
        GetMarketListingResponse res =
                marketListingService.createV3(details.getMember().getId(), request);

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }
    /**
     * 상품 등록 V4
     * @param details
     * @param request
     * @return
     */
    @PostMapping("/api/v4/market-listings")
    public ResponseEntity<ApiResponse<GetMarketListingResponse>> createMarketListingV4(
            @AuthenticationPrincipal PrincipalDetails details, @RequestBody CreateMarketListingRequest request) {
        GetMarketListingResponse res =
                marketListingService.createV3(details.getMember().getId(), request);

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }
    /**
     * 상품 등록 최종버전(Redis Redisson + @RedissonLock AOP + Redis Cache)
     * RedissonLock
     * @param details
     * @param request
     * @return
     */
    @PostMapping("/api/v5/market-listings")
    public ResponseEntity<ApiResponse<GetMarketListingResponse>> createMarketListingV5(
            @AuthenticationPrincipal PrincipalDetails details,
            @RequestBody CreateMarketListingRequest request) {
        GetMarketListingResponse res =
                marketListingService.createV5(details.getMember().getId(), request);

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }

    /**
     *  마켓 상품 전체 조회
     *  sortTotalPrice, sortSaleEndAt 값을 asc/desc 로 전달하여 정렬 조건 추가 가능
     *  asc/desc 가 아닌 값 전달 시 정렬 조건에서 무시 (예외 처리 x)
     */
    @GetMapping("/api/v1/market-listings")
    public ResponseEntity<ApiResponse<PageResponse<SearchAllMarketListingResponse>>> getAllMarketListing(
            @AuthenticationPrincipal PrincipalDetails details,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortTotalPrice,
            @RequestParam(required = false) String sortSaleEndAt,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10);

        return ResponseEntity.ok(ApiResponse.success(
                String.valueOf(HttpStatus.OK.value()),
                marketListingService.getAllMarketListing(
                        details.getMember().getId(), keyword, sortTotalPrice, sortSaleEndAt, pageable)));
    }
    @GetMapping("/api/v2/market-listings")
    public ResponseEntity<ApiResponse<PageResponse<SearchAllMarketListingResponse>>> getAllMarketListingV2(
            @AuthenticationPrincipal PrincipalDetails details,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortTotalPrice,
            @RequestParam(required = false) String sortSaleEndAt,
            @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return ResponseEntity.ok(ApiResponse.success(
                String.valueOf(HttpStatus.OK.value()),
                marketListingService.getAllMarketListingV2(
                        details.getMember().getId(), keyword, sortTotalPrice, sortSaleEndAt, pageable)));
    }
    /**
     * 마켓 상품(거래소) 전체 조회 V3
     * 최종버전(Redis Cache)
     * @param details
     * @param keyword
     * @param sortTotalPrice
     * @param sortSaleEndAt
     * @param page
     * @return
     */
    @GetMapping("/api/v3/market-listings")
    public ResponseEntity<ApiResponse<PageResponse<SearchAllMarketListingResponse>>> getAllMarketListingV3(
            @AuthenticationPrincipal PrincipalDetails details,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortTotalPrice,
            @RequestParam(required = false) String sortSaleEndAt,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10);

        return ResponseEntity.ok(ApiResponse.success(
                String.valueOf(HttpStatus.OK.value()),
                marketListingService.getAllMarketListingV3(
                        details.getMember().getId(), keyword, sortTotalPrice, sortSaleEndAt, pageable)));
    }

    /**
     *  본인 마켓 상품 전체 조회
     *  sortTotalPrice, sortSaleEndAt 값을 asc/desc 로 전달하여 정렬 조건 추가 가능
     *  asc/desc 가 아닌 값 전달 시 정렬 조건에서 무시 (예외 처리 x)
     */
    @GetMapping("/api/v1/me/market-listings")
    public ResponseEntity<ApiResponse<PageResponse<SearchAllMarketListingResponse>>> getAllMeMarketListing(
            @AuthenticationPrincipal PrincipalDetails details,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortTotalPrice,
            @RequestParam(required = false) String sortSaleEndAt,
            @RequestParam(defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, 10);

        return ResponseEntity.ok(ApiResponse.success(
                String.valueOf(HttpStatus.OK.value()),
                marketListingService.getAllMeMarketListing(
                        details.getMember().getId(), keyword, sortTotalPrice, sortSaleEndAt, pageable)));
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

    @GetMapping("/api/v2/market-listings/{marketListingId}")
    public ResponseEntity<ApiResponse<SearchMarketListingResponse>> getMarketListingV2(
            @PathVariable Long marketListingId) {
        return ResponseEntity.ok(ApiResponse.success(
                String.valueOf(HttpStatus.OK), marketListingService.getMarketListingV2(marketListingId)));
    }
    /**
     * 거래소 상품 단건 조회 V3
     * 최종버전(Redis Cache)
     * @param marketListingId
     * @return
     */
    @GetMapping("/api/v3/market-listings/{marketListingId}")
    public ResponseEntity<ApiResponse<SearchMarketListingResponse>> getMarketListingV3(
            @PathVariable Long marketListingId) {
        return ResponseEntity.ok(ApiResponse.success(
                String.valueOf(HttpStatus.OK), marketListingService.getMarketListingV3(marketListingId)));
    }

    /**
     *  인기 검색어 조회
     *  조회수가 높은 마켓 상품 검색 keyword 5개 까지 조회
     *  키워드를 전달할 경우, 해당 키워드로 시작하는 인기검색어 조회
     *  하루 단위로 인기 검색어 캐싱 TTL -> 1일
     */
    @GetMapping("/api/v1/market-listings/search-popular")
    public ResponseEntity<ApiResponse<List<SearchTrendingKeywordResponse>>> getTrendingKeywords(
            @RequestParam(required = false) String prefixKeyword) {
        return ResponseEntity.ok(ApiResponse.success(
                String.valueOf(HttpStatus.OK), marketListingService.getTrendingKeywords(prefixKeyword)));
    }

    /**
     * 마켓 상품(거래소) 등록 취소 V1
     */
    @PatchMapping("/api/v1/market-listings/{marketListingId}")
    public ResponseEntity<ApiResponse<SearchMarketListingResponse>> cancelMarketListing(
            @AuthenticationPrincipal PrincipalDetails details, @PathVariable Long marketListingId) {
        SearchMarketListingResponse res = marketListingService.cancelMarketListing(details, marketListingId);

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK), res));
    }

    /**
     * 마켓 상품(거래소) 등록 취소 V2 - 로컬 캐시
     * @param details
     * @param marketListingId
     * @return
     */
    @PatchMapping("/api/v2/market-listings/{marketListingId}")
    public ResponseEntity<ApiResponse<SearchMarketListingResponse>> cancelMarketListingV2(
            @AuthenticationPrincipal PrincipalDetails details, @PathVariable Long marketListingId) {
        SearchMarketListingResponse res = marketListingService.cancelMarketListingV2(details, marketListingId);

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK), res));
    }


    /**
     * 관리자 마켓 상품 등록 취소
     */
    @PatchMapping("/api/v1/admin/market-listings/{marketListingId}")
    public ResponseEntity<ApiResponse<SearchMarketListingResponse>> cancelMarketListingAdmin(
            @AuthenticationPrincipal PrincipalDetails details, @PathVariable Long marketListingId) {
        SearchMarketListingResponse res = marketListingService.cancelMarketListingAdmin(details, marketListingId);

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK), res));
    }
}
