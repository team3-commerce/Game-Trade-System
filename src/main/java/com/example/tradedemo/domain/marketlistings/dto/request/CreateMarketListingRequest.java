package com.example.tradedemo.domain.marketlistings.dto.request;

import com.example.tradedemo.domain.marketlistings.enums.SalesDurations;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMarketListingRequest {
    private Long memberItemId;
    private BigDecimal totalPrice;
    private Long quantity;
    private SalesDurations salesDuration;
}
