package com.example.tradedemo.domain.marketlistings.dto.request;

import java.math.BigDecimal;

public class CreateMarketListingRequest {

    private Long memberItemId;
    private BigDecimal totalPrice;
    private Long quantity;
    private Long saleDurationHours; // 12 / 24 / 48


    public CreateMarketListingRequest() {}

    public Long getMemberItemId() {
        return memberItemId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Long getSaleDurationHours() {
        return saleDurationHours;
    }
}