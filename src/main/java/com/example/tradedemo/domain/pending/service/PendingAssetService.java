package com.example.tradedemo.domain.pending.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.members.service.MemberItemCacheService;
import com.example.tradedemo.domain.order.entity.Order;
import com.example.tradedemo.domain.pending.dto.PendingAssetResponse;
import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.enums.PendingType;
import com.example.tradedemo.domain.pending.enums.Type;
import com.example.tradedemo.domain.pending.repository.PendingAssetRepository;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.entity.WalletHistories;
import com.example.tradedemo.domain.wallet.enums.WalletStatus;
import com.example.tradedemo.domain.wallet.repository.WalletHistoryRepository;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PendingAssetService {

    private final PendingAssetRepository pendingAssetRepository;
    private final WalletRepository walletRepository;
    private final MemberItemRepository memberItemRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final WalletHistoryRepository walletHistoryRepository;

    private final PendingAssetLockService pendingAssetLockService;
    private final PendingAssetTransactionalService pendingAssetTransactionalService;

    /**
     * 수령 대기 테이블 조회
     * memberId 기준으로 아직 수령하지 않은 것(돈/아이템)을 조회한다.
     */
    @Transactional(readOnly = true)
    public List<PendingAssetResponse> getPendingAssets(Long memberId) {

        List<PendingAsset> assets = pendingAssetRepository
                .findByMemberIdAndIsClaimedFalse(memberId);

        return assets.stream()
                .map(PendingAssetResponse::from)
                .collect(Collectors.toList());
    }
    /**
     * 개별 수령하기
     * pendingAsset을 실제 자산으로 지급한다.
     */
    @Transactional
    public void claimPendingAsset(Long memberId, Long pendingAssetId) {
        /**
         * pendingAsset 조회
         * 동시에 본인의 자산인지 확인
         */
        PendingAsset asset = pendingAssetRepository
                        .findByIdAndMemberIdWithLock(pendingAssetId, memberId)
                        .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_PENDING_ASSET_FORBIDDEN));
        /**
         * 수령 여부 확인
         */
        if (asset.getIsClaimed()) {
            throw new ServiceException(ErrorEnum.ERR_PENDING_ASSET_FOUND_EXCEPTION);
        }
        /**
         * 만료 상태인지 확인
         */
        if (asset.getPendingType() == PendingType.EXPIRED) {
            throw new ServiceException(ErrorEnum.ERR_PENDING_ASSET_EXPIRED_EXCEPTION);
        }
        /**
         * 돈 수령 처리
         * 판매자가 거래 금액을 지갑으로 받는다. 지갑 없다는 에러
         */
        if (asset.getType() == Type.MONEY) {
            Wallet wallet = walletRepository.findByMemberId(memberId)
                    .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_WALLET_NOT_FOUND));

            wallet.addBalance(asset.getMoneyAmount());
            /**
             * 지갑 기록
             */
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
        /**
         * 아이템 수령 처리
         * 구매자가 아이템을 인벤토리에 받는다.
         * 단, 동일한 아이템이라면 수량을 증가하고, 아닐 경우 인벤토리에 추가한다.
         */
        if (asset.getType() == Type.ITEM) {
            Long itemId = asset.getMarketListing()
                    .getMemberItem()
                    .getItem()
                    .getId();

            Long quantity = asset.getItemQuantity();
            /**
             * 인벤토리에 동일한 item이 있는지 확인(없으면 null 반환)
             */
            MemberItem memberItem = memberItemRepository
                            .findByMemberIdAndItemId(memberId, itemId)
                            .orElse(null);
            /**
             * 동일 아이템이 있는 경우 → 수량 증가
             */
            if (memberItem != null) {
                memberItem.increase(quantity);
            } else {
                /**
                 * 동일 아이템이 없는 경우 → 새로운 인벤토리 생성
                 * 인벤토리(MemberItem)의 create : Member member, Item item, LocalDateTime acquiredAt, Long quantity
                 * getReferenceById : 지연로딩, 매서드 호출 기능
                 */
                Member member = memberRepository.getReferenceById(memberId);
                Item item = itemRepository.getReferenceById(itemId);

                MemberItem newItem = MemberItem.create(member, item, LocalDateTime.now(), quantity);
                memberItemRepository.save(newItem);
            }
        }
        /**
         * 수령 처리
         */
        asset.setClaimed(true);
        asset.setClaimedAt(LocalDateTime.now());
    }

    /**
     * Redis 락 안에 비관적 락 먹어야 한다.
     * 경쟁조건 : 락 > 트랜젝션
     * @param memberId
     * @param pendingAssetId
     */
    public void claimPendingAssetV2(Long memberId, Long pendingAssetId) {
        /**
         * Redis 락
         * @param lockKey 락 키 (예: "pending-asset:1")
         * @param action  락 안에서 실행할 비즈니스 로직 = 기존 비관적 락 V2
         */
        String lockKey = "pending-asset:" + pendingAssetId;

        pendingAssetLockService.executeWithLock(lockKey, () ->
                /**
                 * 트랜젝션 -> 락 구조를 피하기 위해 PendingAssetLockService로 비즈니스로직을 옮겼다.
                 * 다른 클래스에 있어야 락(executeWithLock) -> 트랜젝션
                 */
                pendingAssetTransactionalService.executeWithLockclaimPendingAssetV2Internal(memberId, pendingAssetId)
        );
    }
    /**
     * executeWithLockclaimPendingAssetV2Internal 있던 자리
     * 여기에 있던 거 그대로 복사해서 PendingAssetLockService에 옮김
     */

    /**
     * 상품 구매
     * @param marketListing
     * @param order
     * @param buyer
     */
    @Transactional
    public void claimPendingAssetV3(Long memberId, Long pendingAssetId) {
        PendingAsset asset = pendingAssetRepository
                .findByIdAndMemberId(pendingAssetId, memberId)
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
            Long itemId = asset.getMarketListing()
                    .getMemberItem()
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

            memberItemCacheService.deleteMemberItem(memberId, memberItem.getId());
            memberItemCacheService.deleteMemberItemList(memberId);
        }

        asset.setClaimed(true);
        asset.setClaimedAt(LocalDateTime.now());
    }

    @Transactional
    public void createTradePendingAsset(MarketListing marketListing, Order order, Member buyer) {
        PendingAsset sellerPending = PendingAsset.create(
                PendingType.SALE_SUCCESS,
                Type.MONEY,
                marketListing.getTotalPrice(),
                0L,
                false,
                null,
                LocalDateTime.now().plusDays(1),
                marketListing,
                order,
                marketListing.getMember()
        );

        PendingAsset buyerPending = PendingAsset.create(
                PendingType.PURCHASE_SUCCESS,
                Type.ITEM,
                BigDecimal.ZERO,
                marketListing.getQuantity(),
                false,
                null,
                LocalDateTime.now().plusDays(1),
                marketListing,
                order,
                buyer
        );

        pendingAssetRepository.save(sellerPending);
        pendingAssetRepository.save(buyerPending);
    }
}