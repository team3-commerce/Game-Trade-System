package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketListingService {
    private MarketListingRepository marketListingRepository;
}
