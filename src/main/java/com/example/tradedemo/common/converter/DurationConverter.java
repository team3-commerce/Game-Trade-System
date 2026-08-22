package com.example.tradedemo.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Duration;

@Converter(autoApply = false)
public class DurationConverter implements AttributeConverter<Duration, Long> {
    // Duration → DB (초 단위로 저장)
    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        return duration == null ? null : duration.toSeconds();
    }

    // DB → Duration
    @Override
    public Duration convertToEntityAttribute(Long seconds) {
        return seconds == null ? null : Duration.ofSeconds(seconds);
    }
}
