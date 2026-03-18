package com.example.tradedemo.domain.marketlistings.consts;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public final class MarketListingConsts {
    // domain
    public static final String MARKET_LISTING = "listing:";

    // feature
    public static final String TRENDING_SEARCH = "trending:search:";
    public static final String TRENDING_PREFIX_KEYWORD = "trending:prefix:";
    public static final String FIRST_PAGE = "page:first";


    // config
    public static Duration MARKET_LISTING_CANCEL_PENDING_ASSET_DURATION = Duration.ofDays(3);

    public static final Long SEARCH_DUPLICATE_PREVENT_MINUTES = 15L;
    public static final Long TRENDING_KEYWORD_TIME_LIMIT = 2L;
    public static final Long FIRST_PAGE_TIME_LIMIT = 3L;
    public static final long LISTING_ITEM_TIME_LIMIT = 10L;

    public static final int TRENDING_SEARCH_LIMIT = 5;
    public static final DateTimeFormatter TRENDING_SEARCH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
}
