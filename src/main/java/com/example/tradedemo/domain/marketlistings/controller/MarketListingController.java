package com.example.tradedemo.domain.marketlistings.controller;

import com.example.tradedemo.domain.marketlistings.service.MarketListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MarketListingController {
    private final MarketListingService marketListingService;
}
