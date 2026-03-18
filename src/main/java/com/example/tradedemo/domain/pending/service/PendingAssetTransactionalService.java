package com.example.tradedemo.domain.pending.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.enums.Type;
import com.example.tradedemo.domain.pending.repository.PendingAssetRepository;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.entity.WalletHistories;
import com.example.tradedemo.domain.wallet.enums.WalletStatus;
import com.example.tradedemo.domain.wallet.repository.WalletHistoryRepository;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PendingAssetTransactionalService {
    private final PendingAssetRepository pendingAssetRepository;
    private final WalletRepository walletRepository;
    private final WalletHistoryRepository walletHistoryRepository;
    private final MemberItemRepository memberItemRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CacheManager cacheManager;
    /**
     * Redis 락의 executeWithLock의 비즈니스 로직 : action
     * @param memberId
     * @param pendingAssetId
     */
    @Transactional
    @CacheEvict(cacheNames = "inventoryList", allEntries = true)
    public void executeWithLockclaimPendingAssetV2Internal(Long memberId, Long pendingAssetId) {

        /**
         * 비관적 락
         */
        PendingAsset asset = pendingAssetRepository
                .findByIdAndMemberIdWithLock(pendingAssetId, memberId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_PENDING_ASSET_FORBIDDEN));
        if (asset.getIsClaimed()) {
            throw new ServiceException(ErrorEnum.ERR_PENDING_ASSET_FOUND_EXCEPTION);
        }

        if (asset.getType() == Type.MONEY) {
            Wallet wallet = walletRepository.findByMemberId(memberId)
                    .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_WALLET_NOT_FOUND));

            wallet.addBalance(asset.getMoneyAmount());

            walletHistoryRepository.save(WalletHistories.create(
                    asset.getMoneyAmount(),
                    WalletStatus.PURCHASE,
                    wallet.getBalance(),
                    wallet,
                    null,
                    wallet.getMember(),
                    asset.getOrder()
                    )
            );
        }

        if (asset.getType() == Type.ITEM) {

            Long itemId = asset.getMarketListing().
                    getMemberItem()
                    .getItem()
                    .getId();

            Long quantity = asset.getItemQuantity();

            MemberItem memberItem = memberItemRepository
                    .findByMemberIdAndItemId(memberId, itemId)
                    .orElse(null);

            if (memberItem != null) {
                memberItem.increase(quantity);
            } else {

                Member member = memberRepository.getReferenceById(memberId);
                Item item = itemRepository.getReferenceById(itemId);

                memberItem = MemberItem.create(member, item, LocalDateTime.now(), quantity);
                memberItemRepository.save(memberItem);
            }

            Cache cache = cacheManager.getCache("인벤토리 아이템");
            if (cache != null) {
                String key = "사용자:" + memberId + ":인벤토리:" + memberItem.getId();
                cache.evict(key);
            }
        }
        asset.setClaimed(true);
        asset.setClaimedAt(LocalDateTime.now());
    }
}
