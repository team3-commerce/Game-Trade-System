package com.example.tradedemo.domain.marketlistings.facade;

import static com.example.tradedemo.domain.marketlistings.consts.MarketListingConsts.*;
import static com.example.tradedemo.domain.members.consts.MemberItemConst.INVENTORY_ITEM_CACHE_NAME;
import static com.example.tradedemo.domain.members.consts.MemberItemConst.INVENTORY_LIST_CACHE_NAME;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.annotation.RedisLock;
import com.example.tradedemo.common.annotation.RedissonLock;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.marketlistings.consts.MarketListingConsts;
import com.example.tradedemo.domain.marketlistings.dto.CreateMarketListingRequest;
import com.example.tradedemo.domain.marketlistings.dto.GetMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.SearchMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.service.MarketListingCacheService;
import com.example.tradedemo.domain.marketlistings.service.MarketListingService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.service.MemberItemCacheService;
import com.example.tradedemo.domain.members.service.MemberItemService;
import com.example.tradedemo.domain.members.service.MemberService;
import com.example.tradedemo.domain.pending.service.PendingAssetService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MarketListingFacade {

    private final MarketListingService marketListingService;
    private final MemberService memberService;
    private final MemberItemService memberItemService;
    private final PendingAssetService pendingAssetService;
    private final MarketListingCacheService marketListingCacheService;
    private final MemberItemCacheService memberItemCacheService;

    /**
     * 공통 상품 등록 로직
     */
    private GetMarketListingResponse createInternal(Long memberId, CreateMarketListingRequest request, boolean isPessimisticLock) {
        Member member = memberService.findMember(memberId);
        
        MemberItem memberItem;
        if (isPessimisticLock) {
            memberItem = memberItemService.findByIdForUpdate(request.getMemberItemId());
        } else {
            memberItem = memberItemService.findById(request.getMemberItemId());
        }

        memberItemService.validateAndDecrease(memberItem, member.getId(), request.getQuantity());

        BigDecimal unitPrice = request.getTotalPrice()
                .divide(BigDecimal.valueOf(request.getQuantity()), 0, RoundingMode.DOWN);

        MarketListing marketListing = marketListingService.saveMarketListing(
                memberItem.getItem().getName(),
                request.getTotalPrice(),
                unitPrice,
                request.getQuantity(),
                request.getSalesDuration().getDuration(),
                memberItem,
                member);

        return GetMarketListingResponse.create(marketListing, memberItem.getItem());
    }

    /**
     * 상품 등록 V1 (비관적 락 사용)
     */
    @Transactional
    public GetMarketListingResponse create(Long memberId, CreateMarketListingRequest request) {
        return createInternal(memberId, request, true);
    }

    /**
     * 상품 등록 V2 (로컬 캐시 삭제)
     */
    @Transactional
    @Caching(
            evict = {
                @CacheEvict(cacheNames = INVENTORY_LIST_CACHE_NAME, allEntries = true),
                @CacheEvict(
                        cacheNames = INVENTORY_ITEM_CACHE_NAME,
                        key = "'member:' + #memberId + ':item:' + #request.getMemberItemId()")
            })
    public GetMarketListingResponse createV2(Long memberId, CreateMarketListingRequest request) {
        return createInternal(memberId, request, false);
    }

    /**
     * 상품 등록 V3 (Redis 캐시 삭제)
     */
    @Transactional
    public GetMarketListingResponse createV3(Long memberId, CreateMarketListingRequest request) {
        GetMarketListingResponse res = createInternal(memberId, request, false);
        memberItemCacheService.deleteMemberItemList(memberId);
        memberItemCacheService.deleteMemberItem(memberId, request.getMemberItemId());
        return res;
    }

    /**
     * 상품 등록 V4 (Redis Lettuce Lock)
     */
    @RedisLock(key = "'" + MARKET_LISTING_MEMBER_LOCK_PREFIX + "' + #memberId + ':item:' + #request.getMemberItemId()")
    @Transactional
    public GetMarketListingResponse createV4(Long memberId, CreateMarketListingRequest request) {
        GetMarketListingResponse res = createInternal(memberId, request, false);
        memberItemCacheService.deleteMemberItemList(memberId);
        memberItemCacheService.deleteMemberItem(memberId, request.getMemberItemId());
        return res;
    }

    /**
     * 상품 등록 V5 (Redisson Lock)
     */
    @RedissonLock(key = "'" + MARKET_LISTING_LOCK_PREFIX + "' + #memberId + ':item:' + #request.getMemberItemId()")
    @Transactional
    public GetMarketListingResponse createV5(Long memberId, CreateMarketListingRequest request) {
        GetMarketListingResponse res = createInternal(memberId, request, false);
        memberItemCacheService.deleteMemberItemList(memberId);
        memberItemCacheService.deleteMemberItem(memberId, request.getMemberItemId());
        return res;
    }

    /**
     * 상품 등록 취소 로직
     */
    private SearchMarketListingResponse cancelMarketListingImpl(
            PrincipalDetails details, boolean calledByAdminApi, Long marketListingId) {
        MarketListing marketListing = marketListingService.findMarketListing(marketListingId);

        boolean requestFromOwner = marketListing.getMember().getId().equals(details.getMember().getId());

        if (!requestFromOwner) {
            if (!calledByAdminApi || !details.getMember().getRole().equals(MemberRole.ADMIN)) {
                throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_FORBIDDEN_FROM_CANCEL);
            }
        }

        if (marketListing.getStatus().equals(MarketListingStatus.CANCELLED)) {
            return SearchMarketListingResponse.of(marketListing);
        }

        if (!marketListing.getStatus().equals(MarketListingStatus.SELLING)) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_ILLEGAL_CANCEL_STATUS);
        }

        marketListing.updateStatus(MarketListingStatus.CANCELLED);

        pendingAssetService.createCancelPendingAsset(
                marketListing, 
                marketListing.getMember(), 
                MarketListingConsts.MARKET_LISTING_CANCEL_PENDING_ASSET_DURATION
        );

        return SearchMarketListingResponse.of(marketListing);
    }

    @Transactional
    public SearchMarketListingResponse cancelMarketListing(PrincipalDetails details, Long marketListingId) {
        return cancelMarketListingImpl(details, false, marketListingId);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = MARKET_LISTINGS_FIRST_PAGE_CACHE_NAME, allEntries = true),
            @CacheEvict(cacheNames = MARKET_LISTING_ITEM_CACHE_NAME, key = "'listing:' + #marketListingId")
    })
    public SearchMarketListingResponse cancelMarketListingV2(PrincipalDetails details, Long marketListingId) {
        return cancelMarketListingImpl(details, false, marketListingId);
    }

    @Transactional
    public SearchMarketListingResponse cancelMarketListingV3(PrincipalDetails details, Long marketListingId) {
        marketListingCacheService.deleteMarketListingFirstPage();
        marketListingCacheService.deleteMarketListingItem(marketListingId);
        return cancelMarketListingImpl(details, false, marketListingId);
    }

    @Transactional
    public SearchMarketListingResponse cancelMarketListingAdmin(PrincipalDetails details, Long marketListingId) {
        return cancelMarketListingImpl(details, true, marketListingId);
    }
}
