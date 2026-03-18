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
import com.example.tradedemo.domain.pending.repository.PendingAssetRedisLockRepository;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PendingAssetLockService {

    private final PendingAssetRedisLockRepository pendingAssetRedisLockRepositoryLockRedisRepository;

    private final PendingAssetRepository pendingAssetRepository;
    private final WalletRepository walletRepository;
    private final WalletHistoryRepository walletHistoryRepository;
    private final MemberItemRepository memberItemRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CacheManager cacheManager;

    private static final Duration LOCK_TTL         = Duration.ofSeconds(5);
    private static final int      MAX_RETRY         = 10;
    private static final long     RETRY_INTERVAL_MS = 100L;

    /**
     * 락을 획득하고 실행 후 락 해제
     * 실패 시 100ms 간격으로 최대 10회 재시도
     * @param lockKey 락 키 (예: "pending-asset:1")
     * @param action  락 안에서 실행할 비즈니스 로직
     * executeWithLock(lockKey, () -> {action});
     */
    public void executeWithLock(String lockKey, Runnable action) {
        String lockValue = UUID.randomUUID().toString();

        try {
            acquire(lockKey, lockValue);
            action.run();
        } finally {
            pendingAssetRedisLockRepositoryLockRedisRepository.unlock(lockKey, lockValue);
        }
    }

    private void acquire(String lockKey, String lockValue) {
        for (int i = 0; i < MAX_RETRY; i++) {
            if (pendingAssetRedisLockRepositoryLockRedisRepository.tryLock(lockKey, lockValue, LOCK_TTL)) {
                return;
            }
            try {
                Thread.sleep(RETRY_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ServiceException(ErrorEnum.ERR_LOCK_INTERRUPTED); // 500, 예기치 못한 일 발생
            }
        }
        throw new ServiceException(ErrorEnum.ERR_LOCK_ACQUIRE_FAILED); // 409 요청이 현재 서버 상태와 충돌이 발생하여 처리 불가
    }

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

            Cache cache = cacheManager.getCache("inventoryItem");
            if (cache != null) {
                String key = "member:" + memberId + ":item:" + memberItem.getId();
                cache.evict(key);
            }
        }
        asset.setClaimed(true);
        asset.setClaimedAt(LocalDateTime.now());
    }
}