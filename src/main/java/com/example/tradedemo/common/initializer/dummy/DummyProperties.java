package com.example.tradedemo.common.initializer.dummy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "dummy")
@Component
public class DummyProperties {
    private boolean enabled;
    private BatchType mode;
    private int memberCount;
    private int itemCount;
    private int memberItemCount;
    private int marketListingCount;
}
