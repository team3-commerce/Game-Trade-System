package com.example.tradedemo.domain.coupon.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.Duration;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"days", "hours", "minutes", "seconds"})
public class DurationResponse {

    private final Long days;
    private final Long hours;
    private final Long minutes;
    private final Long seconds;

    private DurationResponse(Long days, Long hours, Long minutes, Long seconds) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public static DurationResponse of(Duration duration) {
        if (duration == null) {
            return null;
        }
        long totalSeconds = duration.toSeconds();
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return new DurationResponse(days, hours, minutes, seconds);
    }
}
