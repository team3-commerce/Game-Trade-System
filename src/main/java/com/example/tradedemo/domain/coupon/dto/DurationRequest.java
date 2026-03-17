package com.example.tradedemo.domain.coupon.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DurationRequest {

    private long days;
    private long hours;
    private long minutes;
    private long seconds;

    // 초 단위로 합산
    public long toSeconds() {
        return (days * 86400) + (hours * 3600) + (minutes * 60) + seconds;
    }
}
