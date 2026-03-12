package com.example.tradedemo.domain.marketlistings.dto.response;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchMarketListingResponse {

    private final Long marketListingId;
    private final String itemName;
    private final BigDecimal totalPrice;
    private final Long quantity;
    private final LocalDateTime saleEndAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static SearchMarketListingResponse of(MarketListing marketListing) {
        return new SearchMarketListingResponse(
                marketListing.getId(),
                marketListing.getItemName(),
                marketListing.getTotalPrice(),
                marketListing.getQuantity(),
                marketListing.getSaleEndAt(),
                marketListing.getCreatedAt(),
                marketListing.getModifiedAt());
    }
}
