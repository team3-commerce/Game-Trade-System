package com.example.tradedemo.domain.marketlistings.enums;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SalesDurations {
    HOURS_12(Duration.ofHours(12)),
    HOURS_24(Duration.ofHours(24)),
    HOURS_48(Duration.ofHours(48));

    private final Duration duration;
}
