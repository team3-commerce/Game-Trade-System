package com.example.tradedemo.domain.marketlistings.dto.response;

import com.example.tradedemo.domain.item.dto.GetItemResponse;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetMarketListingResponse {
    private final Long marketListingId;
    private final BigDecimal totalPrice;
    private final BigDecimal unitPrice;
    private final MarketListingStatus marketListingStatus;
    private final Long quantity;
    private final LocalDateTime saleEndAt;

    private final GetItemResponse item;

    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static GetMarketListingResponse create(MarketListing listing, Item item) {
        return new GetMarketListingResponse(
                listing.getId(),
                listing.getTotalPrice(),
                listing.getUnitPrice(),
                listing.getStatus(),
                listing.getQuantity(),
                listing.getSaleEndAt(),
                GetItemResponse.of(item),
                listing.getCreatedAt(),
                listing.getModifiedAt());
    }
}
