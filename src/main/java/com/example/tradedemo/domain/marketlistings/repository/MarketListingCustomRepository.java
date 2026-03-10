package com.example.tradedemo.domain.marketlistings.repository;

import com.example.tradedemo.domain.marketlistings.dto.response.SearchAllMarketListingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarketListingCustomRepository {

    Page<SearchAllMarketListingResponse> getAllMarketListingWithKeyword(String keyword, Pageable pageable);
}
