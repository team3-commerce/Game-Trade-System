package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.marketlistings.consts.MarketListingConsts;
import com.example.tradedemo.domain.marketlistings.dto.CreateMarketListingRequest;
import com.example.tradedemo.domain.marketlistings.dto.GetMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.SearchMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.SearchTrendingKeywordResponse;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.consts.MemberItemConst;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.members.service.MemberItemCacheService;
import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.enums.PendingType;
import com.example.tradedemo.domain.pending.enums.Type;
import com.example.tradedemo.domain.pending.repository.PendingAssetRepository;
import com.example.tradedemo.domain.wallet.repository.WalletHistoryRepository;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketListingService {
    private final MarketListingRepository marketListingRepository;
    private final MemberItemRepository memberItemRepository;
    private final MemberRepository memberRepository;
    private final MarketListingCacheService marketListingCacheService;
    private final PendingAssetRepository pendingAssetRepository;

    private final MemberItemCacheService memberItemCacheService;

    /**
     * 상품 등록
     */
    @Transactional
    public GetMarketListingResponse create(Long memberId, CreateMarketListingRequest request) {
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        MemberItem memberItem = memberItemRepository
                .findByIdForUpdate(request.getMemberItemId())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_ITEM_NOT_FOUND));
        /**
         * 판매자 검증
         * 아이템 소유자와 등록자가 동일한지 확인
         */
        if (!memberItem.getMember().getId().equals(member.getId())) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_OWNER_MISMATCH);
        }
        /**
         * 수량 검증
         * 가지고 있는 아이템보다 더 많이 팔려고 하는 경우
         */
        if (memberItem.getQuantity() < request.getQuantity()) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_OVER_SELLING);
        }

        /**
         * 인벤토리 차감
         * 작성된 만큼 차감
         */
        memberItem.decrease(request.getQuantity());

        /**
         * 총 가격 입력 시 개별 가격 정해짐(반내림)
         */
        BigDecimal unitPrice = request.getTotalPrice()
                .divide(BigDecimal.valueOf(request.getQuantity()), 0, RoundingMode.DOWN); // 0 방향으로 반내림

        MarketListing marketListing = MarketListing.create(
                memberItem.getItem().getName(),
                request.getTotalPrice(),
                unitPrice,
                request.getQuantity(),
                request.getSalesDuration().getDuration(),
                memberItem,
                member);

        marketListingRepository.save(marketListing);

        return GetMarketListingResponse.create(marketListing, memberItem.getItem());
    }

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(cacheNames = "inventoryList", allEntries = true),
                @CacheEvict(
                        cacheNames = "inventoryItem",
                        key = "'member:' + #memberId + ':item:' + #request.getMemberItemId()")
            })
    public GetMarketListingResponse createV2(Long memberId, CreateMarketListingRequest request) {
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        MemberItem memberItem = memberItemRepository
                .findById(request.getMemberItemId())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_ITEM_NOT_FOUND));

        if (!memberItem.getMember().getId().equals(member.getId())) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_OWNER_MISMATCH);
        }

        if (memberItem.getQuantity() < request.getQuantity()) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_OVER_SELLING);
        }

        memberItem.decrease(request.getQuantity());

        BigDecimal unitPrice = request.getTotalPrice()
                .divide(BigDecimal.valueOf(request.getQuantity()), 0, RoundingMode.DOWN); // 0 방향으로 반내림

        MarketListing marketListing = MarketListing.create(
                memberItem.getItem().getName(),
                request.getTotalPrice(),
                unitPrice,
                request.getQuantity(),
                request.getSalesDuration().getDuration(),
                memberItem,
                member);

        marketListingRepository.saveAndFlush(marketListing);

        return GetMarketListingResponse.create(marketListing, memberItem.getItem());
    }

    @Transactional
    public GetMarketListingResponse createV3(Long memberId, CreateMarketListingRequest request) {
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        MemberItem memberItem = memberItemRepository
                .findById(request.getMemberItemId())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_ITEM_NOT_FOUND));

        if (!memberItem.getMember().getId().equals(member.getId())) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_OWNER_MISMATCH);
        }

        if (memberItem.getQuantity() < request.getQuantity()) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_OVER_SELLING);
        }

        memberItem.decrease(request.getQuantity());

        BigDecimal unitPrice = request.getTotalPrice()
                .divide(BigDecimal.valueOf(request.getQuantity()), 0, RoundingMode.DOWN); // 0 방향으로 반내림

        MarketListing marketListing = MarketListing.create(
                memberItem.getItem().getName(),
                request.getTotalPrice(),
                unitPrice,
                request.getQuantity(),
                request.getSalesDuration().getDuration(),
                memberItem,
                member);

        marketListingRepository.saveAndFlush(marketListing);

        memberItemCacheService.deleteMemberItemList(memberId);
        memberItemCacheService.deleteMemberItem(memberId, memberItem.getId());

        return GetMarketListingResponse.create(marketListing, memberItem.getItem());
    }


    /**
     * 마켓 상품 전체 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<SearchAllMarketListingResponse> getAllMarketListing(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        expireMarketListings(); // 만료 처리

        if (keyword != null && !keyword.isBlank()) {
            marketListingCacheService.cacheSearchKeyword(memberId, keyword);
        }

        return PageResponse.of(marketListingRepository.getAllMarketListingWithKeyword(
                null, keyword, MarketListingStatus.SELLING, sortTotalPrice, sortSaleEndAt, pageable));
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = "marketListingsFirstPage",
            key = "'first'",
            condition = "#pageable.pageNumber == 0 "
                    + "&& (#keyword == null || #keyword.isBlank()) "
                    + "&& (#sortTotalPrice == null || #sortTotalPrice.isBlank()) "
                    + "&& (#sortSaleEndAt == null || #sortSaleEndAt.isBlank())"
    )
    public PageResponse<SearchAllMarketListingResponse> getAllMarketListingV2(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {
        expireMarketListings(); // 만료 처리

        if (keyword != null && !keyword.isBlank()) {
            marketListingCacheService.cacheSearchKeyword(memberId, keyword);
        }

        return PageResponse.of(marketListingRepository.getAllMarketListingWithKeyword(
                null, keyword, MarketListingStatus.SELLING, sortTotalPrice, sortSaleEndAt, pageable));
    }

    /**
     * 본인 마켓 상품 전체 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<SearchAllMarketListingResponse> getAllMeMarketListing(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        return PageResponse.of(marketListingRepository.getAllMarketListingWithKeyword(
                memberId, keyword, null, sortTotalPrice, sortSaleEndAt, pageable));
    }

    /**
     * 마켓 상품 단건 조회
     */
    @Transactional(readOnly = true)
    public SearchMarketListingResponse getMarketListing(Long marketListingId) {
        MarketListing marketListing = marketListingRepository
                .findById(marketListingId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND));

        return SearchMarketListingResponse.of(marketListing);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = "marketListingItem",
            key = "'listing:' + #marketListingId"
    )
    public SearchMarketListingResponse getMarketListingV2(Long marketListingId) {
        MarketListing marketListing = marketListingRepository
                .findById(marketListingId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND));

        return SearchMarketListingResponse.of(marketListing);
    }

    /**
     * 만료 시간
     * 상품 등록 시 만료 시간 체크 : saleEndAt
     * 만료 시간이 되면 SELLING(판매) → EXPIRED(만료)
     */
    @Transactional
    public void expireMarketListings() {

        List<MarketListing> expiredListings = marketListingRepository.findByStatusAndSaleEndAtBefore(
                MarketListingStatus.SELLING, LocalDateTime.now());

        for (MarketListing listing : expiredListings) {
            listing.updateStatus(MarketListingStatus.EXPIRED);
        }
    }

    @Transactional(readOnly = true)
    public List<SearchTrendingKeywordResponse> getTrendingKeywords(String prefixKeyword) {
        if (prefixKeyword == null || prefixKeyword.isBlank()) {
            return marketListingCacheService.getTrendingKeywordList();
        } else {
            return marketListingCacheService.getTrendingKeywordListWithPrefix(prefixKeyword);
        }
    }

    private SearchMarketListingResponse cancelMarketListingImpl(
            PrincipalDetails details, boolean calledByAdminApi, Long marketListingId) {
        MarketListing marketListing = marketListingRepository
                .findById(marketListingId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND));

        boolean requestFromOwner =
                marketListing.getMember().getId().equals(details.getMember().getId());

        // 만약 아이템 주인이 아니라면
        if (!requestFromOwner) {
            // 만약 admin api 호출이 아니랴면 무조건 에러
            if (!calledByAdminApi) {
                throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_FORBIDDEN_FROM_CANCEL);
            }

            // 만약 admin api 호출이 맞다면 admin인지 확인
            if (!details.getMember().getRole().equals(MemberRole.ADMIN)) {
                throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_FORBIDDEN_FROM_CANCEL);
            }
        }

        // 만약 이미 취소 상태라며는 그냥 일찍 돌려줍니다
        if (marketListing.getStatus().equals(MarketListingStatus.CANCELLED)) {
            return SearchMarketListingResponse.of(marketListing);
        }

        // 매물이 판매중 상태인지 확인
        if (!marketListing.getStatus().equals(MarketListingStatus.SELLING)) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_ILLEGAL_CANCEL_STATUS);
        }

        marketListing.updateStatus(MarketListingStatus.CANCELLED);

        PendingAsset pendingAsset = PendingAsset.create(
                PendingType.CANCELLED, // 주문 취소
                Type.ITEM, // 아이템을 돌려주어야 함
                BigDecimal.ZERO, // 돈의 양은 0
                marketListing.getQuantity(), // item 갯수
                false, // claimed 상태가 아님
                null, // claimed된 적이 없으므로 null
                LocalDateTime.now().plus(MarketListingConsts.MARKET_LISTING_CANCEL_PENDING_ASSET_DURATION),
                marketListing, // 매물
                null, // 주문을 통해서 생성 되는 것이 아니므로 null
                memberRepository.getReferenceById(details.getMember().getId()) // 멤버
                );

        pendingAssetRepository.save(pendingAsset);

        // modifiedAt 업데이트 강제하기 위해 flush
        marketListing = marketListingRepository.saveAndFlush(marketListing);

        return SearchMarketListingResponse.of(marketListing);
    }

    @Transactional
    public SearchMarketListingResponse cancelMarketListing(PrincipalDetails details, Long marketListingId) {
        return cancelMarketListingImpl(details, false, marketListingId);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "marketListingsFirstPage", allEntries = true),
            @CacheEvict(cacheNames = "marketListingItem", key = "'listing:' + #marketListingId")
    })
    public SearchMarketListingResponse cancelMarketListingV2(PrincipalDetails details, Long marketListingId) {
        return cancelMarketListingImpl(details, false, marketListingId);
    }

    @Transactional
    public SearchMarketListingResponse cancelMarketListingAdmin(PrincipalDetails details, Long marketListingId) {
        return cancelMarketListingImpl(details, true, marketListingId);
    }

    @Transactional(readOnly = true)
    public MarketListing findMarketListing(Long marketListingId) {
        return marketListingRepository.findById(marketListingId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND)
        );
    }
}
