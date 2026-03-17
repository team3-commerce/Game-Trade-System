package com.example.tradedemo.domain.coupon.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DurationRequest {

    @PositiveOrZero(message = "days는 0 이상이어야 합니다")
    private long days;

    @PositiveOrZero(message = "hours는 0 이상이어야 합니다")
    private long hours;

    @PositiveOrZero(message = "hours는 0 이상이어야 합니다")
    private long minutes;

    @PositiveOrZero(message = "seconds는 0 이상이어야 합니다")
    private long seconds;

    // 초 단위로 합산
    public long toSeconds() {
        return (days * 86400) + (hours * 3600) + (minutes * 60) + seconds;
    }
}
