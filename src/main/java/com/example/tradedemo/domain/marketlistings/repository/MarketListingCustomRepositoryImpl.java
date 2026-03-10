package com.example.tradedemo.domain.marketlistings.repository;

import com.example.tradedemo.domain.marketlistings.dto.response.SearchAllMarketListingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class MarketListingCustomRepositoryImpl implements MarketListingCustomRepository {
    @Override
    public Page<SearchAllMarketListingResponse> getAllMarketListingWithKeyword(String keyword, Pageable pageable) {

        return null;
    }
}
