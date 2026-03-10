package com.example.tradedemo.domain.marketlistings.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum MarketListingStatus {

    SELLING,
    SOLD,
    CLAIMED,
    CANCELLED,
    EXPIRED
}
