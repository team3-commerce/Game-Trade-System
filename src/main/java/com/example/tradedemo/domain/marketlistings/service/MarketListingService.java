package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.domain.marketlistings.dto.request.CreateMarketListingRequest;
import com.example.tradedemo.domain.marketlistings.dto.response.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.exception.CreateMarketListingNotFoundException;
import com.example.tradedemo.domain.marketlistings.exception.MemberItemEqualsNotFoundException;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MarketListingService {
    private final MarketListingRepository marketListingRepository;
    private final MemberItemRepository memberItemRepository;

    /**
     * 상품 등록
     */
    @Transactional
    public Long create(Long memberId, CreateMarketListingRequest request) {

        MemberItem memberItem = memberItemRepository.findById(request.getMemberItemId())
                .orElseThrow(CreateMarketListingNotFoundException::new);
        /**
         * 판매자 검증
         * 아이템 소유자와 등록자가 동일한지 확인
         */
        if (!memberItem.getMember().getId().equals(memberId)) {
            throw new MemberItemEqualsNotFoundException();
        }
        /**
          * 수량 검증
          */
        if (memberItem.getQuantity() < request.getQuantity()) {
            throw new CreateMarketListingNotFoundException();
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

        Duration duration = Duration.ofHours(request.getSaleDurationHours());

        MarketListing listing = MarketListing.create(
                memberItem.getItem().getName(),
                request.getTotalPrice(),
                unitPrice,
                request.getQuantity(),
                duration,
                memberItem,
                memberItem.getMember()
        );


        marketListingRepository.save(listing);

        return listing.getId();
    }

    /**
    * 마켓 상품 전체 조회
    */
    @Transactional(readOnly = true)
    public Page<SearchAllMarketListingResponse> getAllMarketListing(
            String keyword, String sortTotalPrice, String sortSaleEndAt, Pageable pageable) {

        return marketListingRepository.getAllMarketListingWithKeyword(keyword, sortTotalPrice, sortSaleEndAt, pageable);
    }
}
