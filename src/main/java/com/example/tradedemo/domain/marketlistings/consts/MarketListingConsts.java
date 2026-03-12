package com.example.tradedemo.domain.marketlistings.consts;

import java.time.format.DateTimeFormatter;

public final class MarketListingConsts {
    // domain
    public static final String MARKET_LISTING = "listing:";

    // feature
    public static final String TRENDING_SEARCH = "trending:search:";
    public static final String TRENDING_PREFIX_KEYWORD = "trending:prefix:";

    // config
    public static final Long SEARCH_DUPLICATE_PREVENT_MINUTES = 15L;
    public static final Long TRENDING_KEYWORD_TIME_LIMIT = 2L;
    public static final int TRENDING_SEARCH_LIMIT = 5;
    public static final DateTimeFormatter TRENDING_SEARCH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
}
