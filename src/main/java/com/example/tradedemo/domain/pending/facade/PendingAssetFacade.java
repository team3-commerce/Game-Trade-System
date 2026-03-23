package com.example.tradedemo.domain.pending.facade;

import static com.example.tradedemo.domain.members.consts.MemberItemConst.INVENTORY_ITEM_CACHE_NAME;
import static com.example.tradedemo.domain.members.consts.MemberItemConst.INVENTORY_LIST_CACHE_NAME;
import static com.example.tradedemo.domain.pending.consts.PendingAssetConst.*;

import com.example.tradedemo.common.annotation.RedisLock;
import com.example.tradedemo.common.annotation.RedissonLock;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.members.service.MemberItemCacheService;
import com.example.tradedemo.domain.members.service.MemberItemService;
import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.enums.PendingType;
import com.example.tradedemo.domain.pending.enums.Type;
import com.example.tradedemo.domain.pending.service.PendingAssetService;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PendingAssetFacade {

    private final PendingAssetService pendingAssetService;
    private final WalletService walletService;
    private final MemberItemService memberItemService;
    private final MemberItemCacheService memberItemCacheService;
    private final CacheManager cacheManager;

    /**
     * 개별 수령하기 V1
     */
    @Transactional
    public void claimPendingAsset(Long memberId, Long pendingAssetId) {
        PendingAsset asset = pendingAssetService.findByIdAndMemberIdWithLock(pendingAssetId, memberId);

        if (asset.getIsClaimed()) {
            throw new ServiceException(ErrorEnum.ERR_PENDING_ASSET_FOUND_EXCEPTION);
        }
        if (asset.getPendingType() == PendingType.EXPIRED) {
            throw new ServiceException(ErrorEnum.ERR_PENDING_ASSET_EXPIRED_EXCEPTION);
        }

        processClaim(memberId, asset);

        pendingAssetService.markAsClaimed(asset);
    }

    /**
     * 개별 수령하기 V2 (RedisLock)
     */
    @RedisLock(key = "'" + PENDING_ASSET_MEMBER_LOCK_PREFIX + "' + #pendingAssetId")
    @Transactional
    @CacheEvict(cacheNames = INVENTORY_LIST_CACHE_NAME, allEntries = true)
    public void claimPendingAssetV2(Long memberId, Long pendingAssetId) {
        PendingAsset asset = pendingAssetService.findByIdAndMemberId(pendingAssetId, memberId);

        if (asset.getIsClaimed()) {
            throw new ServiceException(ErrorEnum.ERR_PENDING_ASSET_FOUND_EXCEPTION);
        }

        processClaim(memberId, asset);

        // V2용 레거시 캐시 처리
        if (asset.getType() == Type.ITEM) {
            Cache cache = cacheManager.getCache(INVENTORY_ITEM_CACHE_NAME);
            if (cache != null) {
                String key = USER_INVENTORY_KEY_PREFIX + memberId + INVENTORY_KEY_SUFFIX + asset.getMarketListing().getMemberItem().getId();
                cache.evict(key);
            }
        }

        pendingAssetService.markAsClaimed(asset);
    }

    /**
     * 개별 수령하기 V3 (RedissonLock)
     */
    @RedissonLock(key = "'" + PENDING_ASSET_LOCK_PREFIX + "' + #pendingAssetId")
    @Transactional
    public void claimPendingAssetV3(Long memberId, Long pendingAssetId) {
        PendingAsset asset = pendingAssetService.findByIdAndMemberId(pendingAssetId, memberId);

        if (asset.getIsClaimed()) {
            throw new ServiceException(ErrorEnum.ERR_PENDING_ASSET_FOUND_EXCEPTION);
        }

        processClaim(memberId, asset);

        pendingAssetService.markAsClaimed(asset);
    }

    private void processClaim(Long memberId, PendingAsset asset) {
        if (asset.getType() == Type.MONEY) {
            Wallet wallet = walletService.findWallet(memberId);
            walletService.addBalanceWithHistory(wallet, asset.getMoneyAmount(), asset.getOrder());
        }

        if (asset.getType() == Type.ITEM) {
            Long itemId = asset.getMarketListing().getMemberItem().getItem().getId();
            memberItemService.addOrUpdateInventory(memberId, itemId, asset.getItemQuantity());
            
            // 캐시 삭제
            memberItemCacheService.deleteMemberItemList(memberId);
            memberItemCacheService.deleteMemberItem(memberId, asset.getMarketListing().getMemberItem().getId());
        }
    }
}
