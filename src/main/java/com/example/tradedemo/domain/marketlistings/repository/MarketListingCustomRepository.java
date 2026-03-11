package com.example.tradedemo.domain.marketlistings.repository;

import com.example.tradedemo.domain.marketlistings.dto.response.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketListingCustomRepository {

    Page<SearchAllMarketListingResponse> getAllMarketListingWithKeyword(
            Long memberId,
            String keyword,
            MarketListingStatus listingStatus,
            String sortTotalPrice,
            String sortSaleEndAt,
            Pageable pageable);
}
