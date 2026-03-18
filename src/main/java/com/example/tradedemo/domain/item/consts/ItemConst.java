package com.example.tradedemo.domain.item.consts;

import java.time.Duration;

public final class ItemConst {

    public static final String ITEM_CACHE_PREFIX = "item:single:";
    public static final String ITEM_CACHE_LIST_PREFIX = "item:list:";

    public static final Duration ITEM_CACHE_TTL = Duration.ofMinutes(60);
    public static final Duration ITEM_CACHE_LIST_TTL = Duration.ofMinutes(60);
}
