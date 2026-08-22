package com.example.tradedemo.domain.marketlistings.dto;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchMarketListingResponse {

    private Long marketListingId;
    private String itemName;
    private BigDecimal totalPrice;
    private Long quantity;
    private MarketListingStatus status;
    private LocalDateTime saleEndAt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String sellerEmail;
    private String sellerNickname;

    public static SearchMarketListingResponse of(MarketListing marketListing) {
        String sellerEmail = null;
        String sellerNickname = null;
        if (marketListing.getMember() != null) {
            sellerEmail = marketListing.getMember().getEmail();
            sellerNickname = marketListing.getMember().getNickname();
        }
        return new SearchMarketListingResponse(
                marketListing.getId(),
                marketListing.getItemName(),
                marketListing.getTotalPrice(),
                marketListing.getQuantity(),
                marketListing.getStatus(),
                marketListing.getSaleEndAt(),
                marketListing.getCreatedAt(),
                marketListing.getModifiedAt(),
                sellerEmail,
                sellerNickname
        );
    }
}
