package com.example.tradedemo.domain.members.consts;

public final class MemberItemConst {

    public static final String MEMBER_INVENTORY = "inventoryList:member:";
    public static final String MEMBER_INVENTORY_ITEM = "inventoryItem:";

    public static final Long INVENTORY_LIST_TIME_LIMIT = 60L;
    public static final Long INVENTORY_ITEM_TIME_LIMIT = 60L;

    // 캐시 이름
    public static final String INVENTORY_LIST_CACHE_NAME = "inventoryList";
    public static final String INVENTORY_ITEM_CACHE_NAME = "inventoryItem";

    // 키 세그먼트
    public static final String PAGE_SEGMENT = ":page:";
    public static final String MEMBER_SEGMENT = "member:";
    public static final String ITEM_SEGMENT = ":item:";
}
