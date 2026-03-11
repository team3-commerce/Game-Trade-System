package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.marketlistings.dto.request.CreateMarketListRequest;
import com.example.tradedemo.domain.marketlistings.dto.response.GetMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.response.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.response.SearchMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.exception.MarketListingNotFoundException;
import com.example.tradedemo.domain.marketlistings.exception.MarketListingOverSellingException;
import com.example.tradedemo.domain.marketlistings.exception.MarketListingOwnerMismatchException;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.exception.MemberItemNotFoundException;
import com.example.tradedemo.domain.members.exception.MemberNotFoundException;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Transactional()
    public GetMarketListingResponse createMarketListing(Long memberId, CreateMarketListRequest req) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        MemberItem memberItem =
                memberItemRepository.findById(req.getMemberItemId()).orElseThrow(MemberItemNotFoundException::new);

        // 만약 상품 등록 요청을 한 회원이 이 아이템의 주인과 같지 않다면 거절
        if (!member.getId().equals(memberItem.getMember().getId())) {
            throw new MarketListingOwnerMismatchException();
        }

        // 가지고 있는 아이템 보다 더 많이 팔려고 할경우 거절
        if (memberItem.getQuantity() < req.getQuantity()) {
            throw new MarketListingOverSellingException();
        }

        Item item = memberItem.getItem();

        String itemName = item.getName();

        // TODO: 저희가 아직 BigDecimal의 scale을 정하지 않았기 때문에
        // 임의로 scale을 설정했습니다.
        BigDecimal unitPrice = req.getTotalPrice().divide(new BigDecimal(req.getQuantity()), 2, RoundingMode.HALF_UP);

        MarketListing marketListing = MarketListing.create(
                itemName,
                req.getTotalPrice(),
                unitPrice,
                req.getQuantity(),
                req.getSalesDuration().getDuration(),
                memberItem,
                member);

        marketListing = marketListingRepository.saveAndFlush(marketListing);

        return GetMarketListingResponse.create(marketListing, item);
    }

    @Transactional(readOnly = true)
    public Page<SearchAllMarketListingResponse> getAllMarketListing(
            String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        return marketListingRepository.getAllMarketListingWithKeyword(
                null, keyword, sortTotalPrice, sortSaleEndAt, pageable);
    }

    /**
     * 본인 마켓 상품 전체 조회
     */
    @Transactional(readOnly = true)
    public Page<SearchAllMarketListingResponse> getAllMeMarketListing(
            Long memberId, String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        return marketListingRepository.getAllMarketListingWithKeyword(
                memberId, keyword, sortTotalPrice, sortSaleEndAt, pageable);
    }

    @Transactional(readOnly = true)
    public SearchMarketListingResponse getMarketListing(Long marketListingId) {
        MarketListing marketListing =
                marketListingRepository.findById(marketListingId).orElseThrow(MarketListingNotFoundException::new);

        return SearchMarketListingResponse.of(marketListing);
    }
}
