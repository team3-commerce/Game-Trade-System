package com.example.tradedemo.domain.order.service;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.order.dto.GetTransactionResponse;
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

    /**
     * 주문 생성
     */
    @Transactional
    public Order createOrder(Member buyer, MarketListing marketListing) {
        Order order = Order.create(
                marketListing.getTotalPrice(),
                marketListing.getQuantity(),
                marketListing.getMember(),  // 구매자
                buyer,
                marketListing,
                marketListing.getMemberItem().getItem() // 거래 매물의 인벤토리의 아이템도감 id
                );

        orderRepository.save(order);

        return order;
    }
}
