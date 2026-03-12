package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.domain.marketlistings.consts.MarketListingConstants;
import com.example.tradedemo.domain.marketlistings.dto.request.CreateMarketListingRequest;
import com.example.tradedemo.domain.marketlistings.dto.response.GetMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.response.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.response.SearchMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.response.SearchTrendingKeywordResponse;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.exception.MarketListingCancelException;
import com.example.tradedemo.domain.marketlistings.exception.MarketListingNotFoundException;
import com.example.tradedemo.domain.marketlistings.exception.MarketListingOverSellingException;
import com.example.tradedemo.domain.marketlistings.exception.MarketListingOwnerMismatchException;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.entity.MemberRole;
import com.example.tradedemo.domain.members.exception.MemberItemNotFoundException;
import com.example.tradedemo.domain.members.exception.MemberNotFoundException;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.enums.PendingType;
import com.example.tradedemo.domain.pending.enums.Type;
import com.example.tradedemo.domain.pending.repository.PendingRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    private final PendingRepository pendingRepository;

    /**
     * 상품 등록
     */
    @Transactional
    public GetMarketListingResponse create(Long memberId, CreateMarketListingRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        MemberItem memberItem =
                memberItemRepository.findById(request.getMemberItemId()).orElseThrow(MemberItemNotFoundException::new);
        /**
         * 판매자 검증
         * 아이템 소유자와 등록자가 동일한지 확인
         */
        if (!memberItem.getMember().getId().equals(member.getId())) {
            throw new MarketListingOwnerMismatchException();
        }
        /**
         * 수량 검증
         * 가지고 있는 아이템보다 더 많이 팔려고 하는 경우
         */
        if (memberItem.getQuantity() < request.getQuantity()) {
            throw new MarketListingOverSellingException();
        }

        /**
         * 인벤토리 차감
         * 2개 이상 존재할 경우 작성된 만큼 차감
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

        marketListingRepository.saveAndFlush(marketListing);

        return GetMarketListingResponse.create(marketListing, memberItem.getItem());
    }

    /**
     * 마켓 상품 전체 조회
     */
    @Transactional(readOnly = true)
    public Page<SearchAllMarketListingResponse> getAllMarketListing(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            marketListingCacheService.cacheSearchKeyword(memberId, keyword);
        }

        return marketListingRepository.getAllMarketListingWithKeyword(
                null, keyword, MarketListingStatus.SELLING, sortTotalPrice, sortSaleEndAt, pageable);
    }

    /**
     * 본인 마켓 상품 전체 조회
     */
    @Transactional(readOnly = true)
    public Page<SearchAllMarketListingResponse> getAllMeMarketListing(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        return marketListingRepository.getAllMarketListingWithKeyword(
                memberId, keyword, null, sortTotalPrice, sortSaleEndAt, pageable);
    }

    @Transactional(readOnly = true)
    public SearchMarketListingResponse getMarketListing(Long marketListingId) {
        MarketListing marketListing =
                marketListingRepository.findById(marketListingId).orElseThrow(MarketListingNotFoundException::new);

        return SearchMarketListingResponse.of(marketListing);
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
        MarketListing marketListing =
                marketListingRepository.findById(marketListingId).orElseThrow(MarketListingNotFoundException::new);

        boolean requestFromOwner =
                marketListing.getMember().getId().equals(details.getMember().getId());

        // 만약 아이템 주인이 아니라면
        if (!requestFromOwner) {
            // 만약 admin api 호출이 아니랴면 무조건 에러
            if (!calledByAdminApi) {
                throw new MarketListingCancelException(ErrorEnum.ERR_MARKET_LISTING_FORBIDDEN_FROM_CANCEL);
            }

            // 만약 admin api 호출이 맞다면 admin인지 확인
            if (!details.getMember().getRole().equals(MemberRole.ADMIN)) {
                throw new MarketListingCancelException(ErrorEnum.ERR_MARKET_LISTING_FORBIDDEN_FROM_CANCEL);
            }
        }

        // 만약 이미 취소 상태라며는 그냥 일찍 돌려줍니다
        if (marketListing.getStatus().equals(MarketListingStatus.CANCELLED)) {
            return SearchMarketListingResponse.of(marketListing);
        }

        // 매물이 판매중 상태인지 확인
        if (!marketListing.getStatus().equals(MarketListingStatus.SELLING)) {
            throw new MarketListingCancelException(ErrorEnum.ERR_MARKET_LISTING_ILLEGAL_CANCEL_STATUS);
        }

        marketListing.updateStatus(MarketListingStatus.CANCELLED);

        PendingAsset pendingAsset = PendingAsset.create(
                PendingType.CANCELLED, // 주문 취소
                Type.ITEM, // 아이템을 돌려주어야 함
                BigDecimal.ZERO, // 돈의 양은 0
                marketListing.getQuantity(), // item 갯수
                false, // claimed 상태가 아님
                null, // claimed된 적이 없으므로 null
                LocalDateTime.now().plus(MarketListingConstants.MARKET_LISTING_CANCEL_PENDING_ASSET_DURATION),
                marketListing, // 매물
                null, // 주문을 통해서 생성 되는 것이 아니므로 null
                memberRepository.getReferenceById(details.getMember().getId()) // 멤버
                );

        pendingRepository.save(pendingAsset);

        // modifiedAt 업데이트 강제하기 위해 flush
        marketListing = marketListingRepository.saveAndFlush(marketListing);

        return SearchMarketListingResponse.of(marketListing);
    }

    @Transactional
    public SearchMarketListingResponse cancelMarketListing(PrincipalDetails details, Long marketListingId) {
        return cancelMarketListingImpl(details, false, marketListingId);
    }

    @Transactional
    public SearchMarketListingResponse cancelMarketListingAdmin(PrincipalDetails details, Long marketListingId) {
        return cancelMarketListingImpl(details, true, marketListingId);
    }
}
