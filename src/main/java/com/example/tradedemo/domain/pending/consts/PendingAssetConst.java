package com.example.tradedemo.domain.pending.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PendingAssetConst {

    // 락 프리픽스
    public static final String PENDING_ASSET_LOCK_PREFIX = "lock:pending-asset:";
    public static final String PENDING_ASSET_MEMBER_LOCK_PREFIX = "pending-asset:";

    // 캐시 관련 (레거시/로컬 캐시용)
    public static final String INVENTORY_ITEM_CACHE_NAME = "인벤토리 아이템";
    public static final String USER_INVENTORY_KEY_PREFIX = "사용자:";
    public static final String INVENTORY_KEY_SUFFIX = ":인벤토리:";
}
