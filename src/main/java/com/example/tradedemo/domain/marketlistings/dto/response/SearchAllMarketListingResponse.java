package com.example.tradedemo.domain.marketlistings.dto.response;

import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchAllMarketListingResponse {
    private final Long marketListingId;
    private final String itemName;
    private final BigDecimal totalPrice;
    private final Long quantity;
    private final MarketListingStatus status;
    private final LocalDateTime saleEndAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
}
