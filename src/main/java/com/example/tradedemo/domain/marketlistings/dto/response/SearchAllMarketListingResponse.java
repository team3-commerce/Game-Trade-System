package com.example.tradedemo.domain.marketlistings.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchAllMarketListingResponse {
    private final Long marketListingId;
    private final String itemName;
    private final BigDecimal totalPrice;
    private final Long quantity;
    private final LocalDateTime saleEndAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
}
