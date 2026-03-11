package com.example.tradedemo.domain.marketlistings.dto.request;

import com.example.tradedemo.domain.marketlistings.enums.SalesDurations;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// TODO: add validation
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMarketListRequest {
    private Long memberItemId;
    private Long quantity;
    private BigDecimal totalPrice;
    private SalesDurations salesDuration;
}
