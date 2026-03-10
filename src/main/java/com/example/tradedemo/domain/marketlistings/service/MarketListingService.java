package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.domain.marketlistings.dto.response.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketListingService {
    private MarketListingRepository marketListingRepository;

    @Transactional
    public Page<SearchAllMarketListingResponse> getAllMarketListing(String keyword) {
        Pageable pageable = PageRequest.of(0, 10);
        return null;
    }
}
