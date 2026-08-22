package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.annotation.RedisLock;
import com.example.tradedemo.common.annotation.RedissonLock;
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
import org.springframework.data.domain.Page;
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
     * 상품 등록 V1
     * @param memberId
     * @param request
     * @return
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
                memberItem.getItem().getName().toLowerCase(),
                request.getTotalPrice(),
                unitPrice,
                request.getQuantity(),
                request.getSalesDuration().getDuration(),
                memberItem,
                member);

        marketListingRepository.save(marketListing);

        return GetMarketListingResponse.create(marketListing, memberItem.getItem());
    }

    /**
     * 상품 등록 V2 : 로컬 인벤토리 조회 캐시
     * @param memberId
     * @param request
     * @return
     */
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

    /**
     * 상품 등록 V3 : Redis 인벤토리 조회 삭제
     * @param memberId
     * @param request
     * @return
     */
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
     * 상품 등록 V4 - Redis RedisLock + @RedisLock AOP + Redis 캐시 삭제
     * @RedisLock : 락 획득 → 트랜잭션 → 락 해제 순서 보장
     * @Transactional : 락 안에서 트랜잭션 실행
     * 사용자ID와 인벤토리 ID(인벤토리에저장된도감번호)
     */
    @RedisLock(key = "'market-listing:member:' + #memberId + ':item:' + #request.getMemberItemId()")
    @Transactional
    public GetMarketListingResponse createV4(Long memberId, CreateMarketListingRequest request) {
        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
        /**
         * Redis 락이 동시성 보장 : findById 이거면 충분하다
         */
        MemberItem memberItem = memberItemRepository
                .findById(request.getMemberItemId())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_ITEM_NOT_FOUND));

        if (!memberItem.getMember().getId().equals(member.getId())) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_OWNER_MISMATCH);
        }

        if (memberItem.getQuantity() < request.getQuantity()) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_OVER_SELLING);
        }

        /**
         * 상품 등록 후 인벤토리 내 수량만큼 삭제(삭제 후에 등록)
         */
        memberItem.decrease(request.getQuantity());

        BigDecimal unitPrice = request.getTotalPrice()
                .divide(BigDecimal.valueOf(request.getQuantity()), 0, RoundingMode.DOWN);
        /**
         * 거래소(MarketListing)에 상품 등록
         */
        MarketListing marketListing = MarketListing.create(
                memberItem.getItem().getName(),
                request.getTotalPrice(),
                unitPrice,
                request.getQuantity(),
                request.getSalesDuration().getDuration(),
                memberItem,
                member
        );

        marketListingRepository.saveAndFlush(marketListing);

        // Redis 캐시 삭제
        memberItemCacheService.deleteMemberItemList(memberId);
        memberItemCacheService.deleteMemberItem(memberId, memberItem.getId());

        return GetMarketListingResponse.create(marketListing, memberItem.getItem());
    }

    /**
     * 상품 등록 V5 - Redis Redisson + @RedissonLock AOP + Redis 캐시 삭제
     * 인벤토리 조회 캐시 삭제
     * @param memberId
     * @param request
     * @return
     */
    @RedissonLock(key = "'lock:market-listing:member:' + #memberId + ':item:' + #request.getMemberItemId()")
    @Transactional
    public GetMarketListingResponse createV5(Long memberId, CreateMarketListingRequest request) {
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
                .divide(BigDecimal.valueOf(request.getQuantity()), 0, RoundingMode.DOWN);

        MarketListing marketListing = MarketListing.create(
                memberItem.getItem().getName(),
                request.getTotalPrice(),
                unitPrice,
                request.getQuantity(),
                request.getSalesDuration().getDuration(),
                memberItem,
                member
        );

        marketListingRepository.saveAndFlush(marketListing);

        // Redis 캐시 삭제
        memberItemCacheService.deleteMemberItemList(memberId);
        memberItemCacheService.deleteMemberItem(memberId, memberItem.getId());

        return GetMarketListingResponse.create(marketListing, memberItem.getItem());
    }

    /**
     * 마켓 상품 전체 조회 V1
     * @param memberId
     * @param keyword
     * @param sortTotalPrice
     * @param sortSaleEndAt
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PageResponse<SearchAllMarketListingResponse> getAllMarketListing(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        if (keyword != null && !keyword.isBlank()) {
            marketListingCacheService.cacheSearchKeyword(memberId, keyword);
        }

        return PageResponse.of(marketListingRepository.getAllMarketListingWithKeyword(
                null, keyword, MarketListingStatus.SELLING, sortTotalPrice, sortSaleEndAt, pageable));
    }

    /**
     * 마켓 상품 전체 조회 V2 - 로컬 캐시
     * @param memberId
     * @param keyword
     * @param sortTotalPrice
     * @param sortSaleEndAt
     * @param pageable
     * @return
     */
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

        if (keyword != null && !keyword.isBlank()) {
            marketListingCacheService.cacheSearchKeyword(memberId, keyword);
        }

        return PageResponse.of(marketListingRepository.getAllMarketListingWithKeyword(
                null, keyword, MarketListingStatus.SELLING, sortTotalPrice, sortSaleEndAt, pageable));
    }

    /**
     * 마켓 상품 전체 조회 V3 — Redis 캐시
     * @param memberId
     * @param keyword
     * @param sortTotalPrice
     * @param sortSaleEndAt
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public PageResponse<SearchAllMarketListingResponse> getAllMarketListingV3(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        boolean isCacheable = pageable.getPageNumber() == 0
                && (keyword == null || keyword.isBlank())
                && (sortTotalPrice == null || sortTotalPrice.isBlank())
                && (sortSaleEndAt == null || sortSaleEndAt.isBlank());

        if (isCacheable) {
            PageResponse<SearchAllMarketListingResponse> cached =
                    marketListingCacheService.getMarketListingFirstPage();
            if (cached != null) return cached;
        }

        if (keyword != null && !keyword.isBlank()) {
            marketListingCacheService.cacheSearchKeyword(memberId, keyword);
        }

        PageResponse<SearchAllMarketListingResponse> response = PageResponse.of(
                marketListingRepository.getAllMarketListingWithKeyword(
                        null, keyword, MarketListingStatus.SELLING,
                        sortTotalPrice, sortSaleEndAt, pageable));

        if (isCacheable) {
            marketListingCacheService.setMarketListingFirstPage(response);
        }

        return response;
    }

    /**
     * 본인 마켓 상품 전체 조회 V1
     */
    @Transactional(readOnly = true)
    public PageResponse<SearchAllMarketListingResponse> getAllMeMarketListing(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        return PageResponse.of(marketListingRepository.getAllMarketListingWithKeyword(
                memberId, keyword, null, sortTotalPrice, sortSaleEndAt, pageable));
    }

    /**
     * 마켓 상품 단건 조회V1
     * @param marketListingId
     * @return
     */
    @Transactional(readOnly = true)
    public SearchMarketListingResponse getMarketListing(Long marketListingId) {
        MarketListing marketListing = marketListingRepository
                .findById(marketListingId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND));

        return SearchMarketListingResponse.of(marketListing);
    }
    /**
     * 마켓 상품 단건 조회 V2 - 로컬 캐시
     * @param marketListingId
     * @return
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "marketListingItem", key = "'listing:' + #marketListingId")
    public SearchMarketListingResponse getMarketListingV2(Long marketListingId) {

        MarketListing marketListing = marketListingRepository.findById(marketListingId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND));

        return SearchMarketListingResponse.of(marketListing);
    }
    /**
     * 마켓 상품 단건 조회 V3 - Redis 캐시
     * @param marketListingId
     * @return
     */
    @Transactional(readOnly = true)
    public SearchMarketListingResponse getMarketListingV3(Long marketListingId) {

        /**
         * Redis 캐시 조회
         */
        SearchMarketListingResponse cached =
                marketListingCacheService.getMarketListingItem(marketListingId);
        if (cached != null) return cached;
        /**
         * DB 조회
         */
        MarketListing marketListing = marketListingRepository
                .findById(marketListingId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND));

        /**
         * 조회 기록 응답 : 단건 조회 가져오기
         */
        SearchMarketListingResponse response = SearchMarketListingResponse.of(marketListing);
        /**
         * Redis 저장
         */
        marketListingCacheService.setMarketListingItem(marketListingId, response);

        return SearchMarketListingResponse.of(marketListing);
    }

    /**
     * 인기 검색어 조회 V1
     * @param prefixKeyword
     * @return
     */
    @Transactional(readOnly = true)
    public List<SearchTrendingKeywordResponse> getTrendingKeywords(String prefixKeyword) {
        if (prefixKeyword == null || prefixKeyword.isBlank()) {
            return marketListingCacheService.getTrendingKeywordList();
        } else {
            return marketListingCacheService.getTrendingKeywordListWithPrefix(prefixKeyword);
        }
    }


    /**
     * 상품 등록 취소 V1 - QueryDSL
     * @param details
     * @param calledByAdminApi
     * @param marketListingId
     * @return
     */
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
    /**
     * 상품 등록 취소 V2 - 로컬 캐시
     * @param details
     * @param marketListingId
     * @return
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "marketListingsFirstPage", allEntries = true),
            @CacheEvict(cacheNames = "marketListingItem", key = "'listing:' + #marketListingId")
    })
    public SearchMarketListingResponse cancelMarketListingV2(PrincipalDetails details, Long marketListingId) {
        return cancelMarketListingImpl(details, false, marketListingId);
    }
    /**
     * 상품 등록 취소 V1 - 관리자
     * @param details
     * @param marketListingId
     * @return
     */
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

    /**
     * 공용 - 마켓(거래소) id 찾기
     * @param marketListingId
     * @return
     */
    @Transactional(readOnly = true)
    public MarketListing findMarketListing(Long marketListingId) {
        return marketListingRepository.findById(marketListingId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND)
        );
    }

    private boolean isDefaultFirstPage(String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {
        return pageable.getPageNumber() == 0
                && isBlank(keyword)
                && isBlank(sortTotalPrice)
                && isBlank(sortSaleEndAt);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
