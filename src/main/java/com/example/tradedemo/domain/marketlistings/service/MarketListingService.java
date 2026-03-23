package com.example.tradedemo.domain.marketlistings.service;

import static com.example.tradedemo.domain.marketlistings.consts.MarketListingConsts.*;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.marketlistings.dto.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.SearchMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.SearchTrendingKeywordResponse;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketListingService {
    private final MarketListingRepository marketListingRepository;
    private final MarketListingCacheService marketListingCacheService;

    /**
     * 마켓 상품 저장
     */
    @Transactional
    public MarketListing saveMarketListing(
            String itemName,
            BigDecimal totalPrice,
            BigDecimal unitPrice,
            Long quantity,
            Duration duration,
            MemberItem memberItem,
            Member member) {

        MarketListing marketListing = MarketListing.create(
                itemName,
                totalPrice,
                unitPrice,
                quantity,
                duration,
                memberItem,
                member);

        return marketListingRepository.save(marketListing);
    }

    /**
     * 특정 회원의 판매 중인 매물 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean hasActiveListings(Long memberId) {
        return marketListingRepository.existsByMemberIdAndStatus(memberId, MarketListingStatus.SELLING);
    }

    /**
     * 마켓 상품 전체 조회 V1
     */
    @Transactional(readOnly = true)
    public PageResponse<SearchAllMarketListingResponse> getAllMarketListing(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        if (keyword != null && !keyword.isBlank()) {
            marketListingCacheService.cacheSearchKeyword(memberId, keyword);
        }

        return PageResponse.of(marketListingRepository.getAllMarketListingWithKeyword(
                null, keyword, MarketListingStatus.SELLING, sortTotalPrice, sortSaleEndAt, pageable));
    }

    /**
     * 마켓 상품 전체 조회 V2 - 로컬 캐시
     */
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = MARKET_LISTINGS_FIRST_PAGE_CACHE_NAME,
            key = "'first'",
            condition = "#pageable.pageNumber == 0 "
                    + "&& (#keyword == null || #keyword.isBlank()) "
                    + "&& (#sortTotalPrice == null || #sortTotalPrice.isBlank()) "
                    + "&& (#sortSaleEndAt == null || #sortSaleEndAt.isBlank())"
    )
    public PageResponse<SearchAllMarketListingResponse> getAllMarketListingV2(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        if (keyword != null && !keyword.isBlank()) {
            marketListingCacheService.cacheSearchKeyword(memberId, keyword);
        }

        return PageResponse.of(marketListingRepository.getAllMarketListingWithKeyword(
                null, keyword, MarketListingStatus.SELLING, sortTotalPrice, sortSaleEndAt, pageable));
    }

    /**
     * 마켓 상품 전체 조회 V3 — Redis 캐시
     */
    @Transactional(readOnly = true)
    public PageResponse<SearchAllMarketListingResponse> getAllMarketListingV3(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        boolean isCacheable = pageable.getPageNumber() == 0
                && (keyword == null || keyword.isBlank())
                && (sortTotalPrice == null || sortTotalPrice.isBlank())
                && (sortSaleEndAt == null || sortSaleEndAt.isBlank());

        if (isCacheable) {
            PageResponse<SearchAllMarketListingResponse> cached =
                    marketListingCacheService.getMarketListingFirstPage();
            if (cached != null) return cached;
        }

        if (keyword != null && !keyword.isBlank()) {
            marketListingCacheService.cacheSearchKeyword(memberId, keyword);
        }

        PageResponse<SearchAllMarketListingResponse> response = PageResponse.of(
                marketListingRepository.getAllMarketListingWithKeyword(
                        null, keyword, MarketListingStatus.SELLING,
                        sortTotalPrice, sortSaleEndAt, pageable));

        if (isCacheable) {
            marketListingCacheService.setMarketListingFirstPage(response);
        }

        return response;
    }

    /**
     * 본인 마켓 상품 전체 조회 V1
     */
    @Transactional(readOnly = true)
    public PageResponse<SearchAllMarketListingResponse> getAllMeMarketListing(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        return PageResponse.of(marketListingRepository.getAllMarketListingWithKeyword(
                memberId, keyword, null, sortTotalPrice, sortSaleEndAt, pageable));
    }

    /**
     * 마켓 상품 단건 조회V1
     */
    @Transactional(readOnly = true)
    public SearchMarketListingResponse getMarketListing(Long marketListingId) {
        MarketListing marketListing = marketListingRepository
                .findById(marketListingId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND));

        return SearchMarketListingResponse.of(marketListing);
    }

    /**
     * 마켓 상품 단건 조회 V2 - 로컬 캐시
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = MARKET_LISTING_ITEM_CACHE_NAME, key = "'listing:' + #marketListingId")
    public SearchMarketListingResponse getMarketListingV2(Long marketListingId) {

        MarketListing marketListing = marketListingRepository.findById(marketListingId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND));

        return SearchMarketListingResponse.of(marketListing);
    }

    /**
     * 마켓 상품 단건 조회 V3 - Redis 캐시
     */
    @Transactional(readOnly = true)
    public SearchMarketListingResponse getMarketListingV3(Long marketListingId) {

        SearchMarketListingResponse cached =
                marketListingCacheService.getMarketListingItem(marketListingId);
        if (cached != null) return cached;

        MarketListing marketListing = marketListingRepository
                .findById(marketListingId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND));

        SearchMarketListingResponse response = SearchMarketListingResponse.of(marketListing);
        marketListingCacheService.setMarketListingItem(marketListingId, response);

        return response;
    }

    /**
     * 인기 검색어 조회 V1
     */
    @Transactional(readOnly = true)
    public List<SearchTrendingKeywordResponse> getTrendingKeywords(String prefixKeyword) {
        if (prefixKeyword == null || prefixKeyword.isBlank()) {
            return marketListingCacheService.getTrendingKeywordList();
        } else {
            return marketListingCacheService.getTrendingKeywordListWithPrefix(prefixKeyword);
        }
    }

    /**
     * 공용 - 마켓(거래소) id 찾기
     */
    @Transactional(readOnly = true)
    public MarketListing findMarketListing(Long marketListingId) {
        return marketListingRepository.findById(marketListingId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND)
        );
    }
}
