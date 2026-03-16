package com.example.tradedemo.domain.order.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.order.dto.CreateOrderResponse;
import com.example.tradedemo.domain.order.dto.GetTransactionResponse;
import com.example.tradedemo.domain.order.entity.Order;
import com.example.tradedemo.domain.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.enums.PendingType;
import com.example.tradedemo.domain.pending.enums.Type;
import com.example.tradedemo.domain.pending.repository.PendingAssetRepository;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.entity.WalletHistories;
import com.example.tradedemo.domain.wallet.enums.WalletStatus;
import com.example.tradedemo.domain.wallet.repository.WalletHistoryRepository;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MarketListingRepository marketListingRepository;
    private final MemberRepository memberRepository;
    private final WalletRepository walletRepository;
    private final PendingAssetRepository pendingAssetRepository;
    private final WalletHistoryRepository walletHistoryRepository;

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
         * 이미 판매된 상품 또는 구매 불가 상태
         */
        if (marketlisting.getStatus() != MarketListingStatus.SELLING) {
            throw new IllegalStateException("이미 판매되었거나 구매할 수 없는 상품입니다.");
        }
        /**
         * 가격
         */
        BigDecimal price = marketlisting.getTotalPrice();
        /**
         * 잔액 비교
         * 구매자의 돈과 총 가격 비교
         */
        if (wallet.getBalance().compareTo(price) < 0) {
            throw new ServiceException(ErrorEnum.ERR_WALLET_INSUFFICIENT_BALANCE_BAD_REQUEST);
        }
        /**
         * 판매자
         */
        Member seller = marketlisting.getMember();

        /**
         * 주문 ID 생성
         */
        Order order = Order.create(
                marketlisting.getTotalPrice(),
                marketlisting.getQuantity(),
                seller,
                buyer,
                marketlisting,
                marketlisting.getMemberItem().getItem() // 거래 매물의 인벤토리의 아이템도감 id
                );

        orderRepository.save(order);
        /**
         * 지갑에서 돈 차감
         */
        wallet.decrease(price);
        /**
         * 구매자 WalletHistory  : 기록
         * price.negate()       : - 가격
         * wallet.getBalance()  : 차감 후 잔액
         */
        walletHistoryRepository.save(WalletHistories.create(
                        price.negate(),
                        WalletStatus.PURCHASE,
                        wallet.getBalance(),
                        wallet,
                        null,
                        wallet.getMember(),
                        order
                )
        );
        /**
         * 판매자 돈 수령 대기
         */
        PendingAsset sellerPending = PendingAsset.create(
                PendingType.SALE_SUCCESS,
                Type.MONEY,
                marketlisting.getTotalPrice(),
                0L,
                false,
                null,
                LocalDateTime.now().plusDays(1),
                marketlisting,
                order,
                seller
        );

        /**
         * 구매자 아이템 수령 대기
         */
        PendingAsset buyerPending = PendingAsset.create(
                PendingType.PURCHASE_SUCCESS,
                Type.ITEM,
                BigDecimal.ZERO,
                marketlisting.getQuantity(),
                false,
                null,
                LocalDateTime.now().plusDays(1),
                marketlisting,
                order,
                buyer
        );

        /**
         * 구매자 수령 대기 상태
         */
        pendingAssetRepository.save(sellerPending);
        /**
         * 판매자 수령 대기 상태
         */
        pendingAssetRepository.save(buyerPending);

        /**
         * 매물 상태 변경 : SELLING → SOLD
         */
        marketlisting.updateStatus(MarketListingStatus.SOLD);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = "marketListingsFirstPage", allEntries = true),
        @CacheEvict(cacheNames = "marketListingItem", key = "'listing:' + #marketListingId")
    })
    public CreateOrderResponse purchaseV2(Long buyerId, Long marketListingId) {
        Member buyer = memberRepository.findById(buyerId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND)
        );
        Wallet wallet = walletRepository.findByMemberId(buyerId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_WALLET_NOT_FOUND)
        );

        MarketListing marketlisting = marketListingRepository.findById(marketListingId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND)
        );

        if (marketlisting.getStatus() != MarketListingStatus.SELLING) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_SELLING);
        }

        BigDecimal price = marketlisting.getTotalPrice();

        if (wallet.getBalance().compareTo(price) < 0) {
            throw new ServiceException(ErrorEnum.ERR_WALLET_INSUFFICIENT_BALANCE_BAD_REQUEST);
        }

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

        wallet.decrease(price);

        walletHistoryRepository.save(WalletHistories.create(
                        price.negate(),
                        WalletStatus.PURCHASE,
                        wallet.getBalance(),
                        wallet,
                        null,
                        wallet.getMember(),
                        order
                )
        );

        PendingAsset sellerPending = PendingAsset.create(
                PendingType.SALE_SUCCESS,
                Type.MONEY,
                marketlisting.getTotalPrice(),
                0L,
                false,
                null,
                LocalDateTime.now().plusDays(1),
                marketlisting,
                order,
                seller
        );

        PendingAsset buyerPending = PendingAsset.create(
                PendingType.PURCHASE_SUCCESS,
                Type.ITEM,
                BigDecimal.ZERO,
                marketlisting.getQuantity(),
                false,
                null,
                LocalDateTime.now().plusDays(1),
                marketlisting,
                order,
                buyer
        );

        pendingAssetRepository.save(sellerPending);
        pendingAssetRepository.save(buyerPending);

        marketlisting.updateStatus(MarketListingStatus.SOLD);

        return CreateOrderResponse.create(order, marketlisting.getItemName());
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
