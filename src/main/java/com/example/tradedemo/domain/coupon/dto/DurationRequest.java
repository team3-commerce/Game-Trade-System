package com.example.tradedemo.domain.coupon.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DurationRequest {

    private final long days;
    private final long hours;
    private final long minutes;
    private final long seconds;

    @JsonCreator
    public DurationRequest(
            @JsonProperty("days") long days,
            @JsonProperty("hours") long hours,
            @JsonProperty("minutes") long minutes,
            @JsonProperty("seconds") long seconds) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    // 초 단위로 합산
    public long toSeconds() {
        return (days * 86400) + (hours * 3600) + (minutes * 60) + seconds;
    }
}
