package com.example.tradedemo.common.initializer.dummy;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!prod")
public class DummyDataRunner implements ApplicationRunner {

    private final DummyDataService dummyDataService;
    private final DummyProperties properties;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if(!properties.isEnabled()) return;

        switch (properties.getMode()) {
            case MEMBER_ONLY -> {
                dummyDataService.createDummyMember(properties.getMemberCount());
            }
            case ITEM_ONLY -> {
                dummyDataService.createDummyItem(properties.getItemCount());
            }
            case MEMBER_ITEM_WITH_BASE -> {
                dummyDataService.createDummyMember(properties.getMemberCount());
                dummyDataService.createDummyItem(properties.getItemCount());
                dummyDataService.createDummyMemberItem(properties.getMemberItemCount());
            }
            case MARKET_LISTING_WITH_BASE -> {
                dummyDataService.createDummyMember(properties.getMemberCount());
                dummyDataService.createDummyItem(properties.getItemCount());
                dummyDataService.createDummyMemberItem(properties.getMemberItemCount());
                dummyDataService.createDummyMarketListing(properties.getMarketListingCount());
            }
        }
    }
}
