package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.domain.marketlistings.dto.response.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.response.SearchMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.response.SearchTrendingKeywordResponse;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.exception.MarketListingNotFoundException;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketListingService {
    private final MarketListingRepository marketListingRepository;
    private final MarketListingCacheService marketListingCacheService;

    @Transactional(readOnly = true)
    public Page<SearchAllMarketListingResponse> getAllMarketListing(
            String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            marketListingCacheService.cacheSearchKeyword(keyword);
        }

        return marketListingRepository.getAllMarketListingWithKeyword(
                null, keyword, MarketListingStatus.SELLING, sortTotalPrice, sortSaleEndAt, pageable);
    }

    /**
     * 본인 마켓 상품 전체 조회
     */
    @Transactional(readOnly = true)
    public Page<SearchAllMarketListingResponse> getAllMeMarketListing(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        return marketListingRepository.getAllMarketListingWithKeyword(
                memberId, keyword, null, sortTotalPrice, sortSaleEndAt, pageable);
    }

    @Transactional(readOnly = true)
    public SearchMarketListingResponse getMarketListing(Long marketListingId) {
        MarketListing marketListing =
                marketListingRepository.findById(marketListingId).orElseThrow(MarketListingNotFoundException::new);

        return SearchMarketListingResponse.of(marketListing);
    }

    @Transactional(readOnly = true)
    public List<SearchTrendingKeywordResponse> getTrendingKeywords(String prefixKeyword) {
        if (prefixKeyword == null || prefixKeyword.isBlank()) {
            return marketListingCacheService.getTrendingKeywordList();
        } else {
            return marketListingCacheService.getTrendingKeywordListWithPrefix(prefixKeyword);
        }
    }
}
