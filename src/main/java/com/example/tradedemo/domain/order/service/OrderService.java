package com.example.tradedemo.domain.order.service;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.order.dto.response.*;
import com.example.tradedemo.domain.order.entity.Order;
import com.example.tradedemo.domain.order.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MarketListingRepository marketListingRepository;
    private final MemberRepository memberRepository;

    /**
     * 상품 구매
     */
    public void purchase(Long buyerId, Long marketListingId) {

        /**
         * 구매자
         */
        Member buyer = memberRepository.findById(buyerId).orElseThrow();
        /**
         * 거래 매물
         */
        MarketListing marketlisting =
                marketListingRepository.findById(marketListingId).orElseThrow();
        /**
         * 판매자
         */
        Member seller = marketlisting.getMember();

        Order order = Order.create(
                marketlisting.getTotalPrice(),
                marketlisting.getQuantity(),
                seller,
                buyer,
                marketlisting,
                marketlisting.getMemberItem().getItem() // 거래 매물의 인벤토리의 아이템도감 id
                );

        orderRepository.save(order);
    }

    /**
     * 내 구매 내역 조회
     */
    @Transactional(readOnly = true)
    public List<GetTransactionResponse> getMyBuyer(Long memberId) {

        return orderRepository.findByBuyerId(memberId).stream()
                .map(GetTransactionResponse::of)
                .toList();
    }

    /**
     * 내 판매 내역 조회
     */
    @Transactional(readOnly = true)
    public List<GetTransactionResponse> getMySeller(Long memberId) {

        return orderRepository.findBySellerId(memberId).stream()
                .map(GetTransactionResponse::of)
                .toList();
    }
}
