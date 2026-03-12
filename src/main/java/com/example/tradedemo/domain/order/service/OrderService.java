package com.example.tradedemo.domain.order.service;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.order.dto.response.*;
import com.example.tradedemo.domain.order.entity.Order;
import com.example.tradedemo.domain.order.exception.WalletInsufficientBalanceException;
import com.example.tradedemo.domain.order.repository.OrderRepository;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import java.math.BigDecimal;
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
    private final WalletRepository walletRepository;

    /**
     * 상품 구매
     */
    public void purchase(Long buyerId, Long marketListingId) {

        /**
         * 구매자
         */
        Member buyer = memberRepository.findById(buyerId).orElseThrow();
        /**
         * 지갑 조회
         * 돈 있는지 확인하고 구매해야 한다.
         */
        Wallet wallet = walletRepository.findByMemberId(buyerId).orElseThrow();
        /**
         * 거래 매물
         */
        MarketListing marketlisting =
                marketListingRepository.findById(marketListingId).orElseThrow();
        /**
         * 가격
         */
        BigDecimal price = marketlisting.getTotalPrice();
        /**
         * 잔액 비교
         * 구매자의 돈과 총 가격 비교
         */
        if (wallet.getBalance().compareTo(price) < 0) {
            throw new WalletInsufficientBalanceException();
        }
        /**
         * 지갑에서 돈 차감
         */
        wallet.decrease(price);
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
    public List<TransactionResponse> getMyBuyer(Long memberId) {

        return orderRepository.findByBuyerId(memberId).stream()
                .map(TransactionResponse::of)
                .toList();
    }

    /**
     * 내 판매 내역 조회
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getMySeller(Long memberId) {

        return orderRepository.findBySellerId(memberId).stream()
                .map(TransactionResponse::of)
                .toList();
    }
}
