package com.example.tradedemo.domain.pending.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.order.entity.Order;
import com.example.tradedemo.domain.pending.dto.PendingAssetResponse;
import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.enums.PendingType;
import com.example.tradedemo.domain.pending.enums.Type;
import com.example.tradedemo.domain.pending.repository.PendingAssetRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PendingAssetService {

    private final PendingAssetRepository pendingAssetRepository;

    /**
     * 수령 대기 테이블 조회
     */
    @Transactional(readOnly = true)
    public List<PendingAssetResponse> getPendingAssets(Long memberId) {
        return pendingAssetRepository.findByMemberIdAndIsClaimedFalse(memberId).stream()
                .map(PendingAssetResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 수령 대기 중인 자산 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean hasUnclaimedAssets(Long memberId) {
        return pendingAssetRepository.existsByMemberIdAndIsClaimedFalse(memberId);
    }

    @Transactional(readOnly = true)
    public PendingAsset findByIdAndMemberId(Long pendingAssetId, Long memberId) {
        return pendingAssetRepository.findByIdAndMemberId(pendingAssetId, memberId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_PENDING_ASSET_FORBIDDEN));
    }

    @Transactional(readOnly = true)
    public PendingAsset findByIdAndMemberIdWithLock(Long pendingAssetId, Long memberId) {
        return pendingAssetRepository.findByIdAndMemberIdWithLock(pendingAssetId, memberId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_PENDING_ASSET_FORBIDDEN));
    }

    @Transactional
    public void markAsClaimed(PendingAsset asset) {
        asset.setClaimed(true);
        asset.setClaimedAt(LocalDateTime.now());
    }

    @Transactional
    public void createCancelPendingAsset(MarketListing marketListing, Member owner, Duration duration) {
        PendingAsset pendingAsset = PendingAsset.create(
                PendingType.CANCELLED,
                Type.ITEM,
                BigDecimal.ZERO,
                marketListing.getQuantity(),
                false,
                null,
                LocalDateTime.now().plus(duration),
                marketListing,
                null,
                owner
        );

        pendingAssetRepository.save(pendingAsset);
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
