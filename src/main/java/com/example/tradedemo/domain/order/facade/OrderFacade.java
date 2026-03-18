package com.example.tradedemo.domain.order.facade;


import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.service.MarketListingCacheService;
import com.example.tradedemo.domain.marketlistings.service.MarketListingService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.service.MemberService;
import com.example.tradedemo.domain.order.dto.CreateOrderResponse;
import com.example.tradedemo.domain.order.entity.Order;
import com.example.tradedemo.domain.order.service.OrderService;
import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.enums.PendingType;
import com.example.tradedemo.domain.pending.enums.Type;
import com.example.tradedemo.domain.pending.service.PendingAssetService;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.entity.WalletHistories;
import com.example.tradedemo.domain.wallet.enums.WalletStatus;
import com.example.tradedemo.domain.wallet.repository.WalletHistoryRepository;
import com.example.tradedemo.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    
    private final OrderService orderService;
    private final WalletService walletService;
    private final MemberService memberService;
    private final PendingAssetService pendingAssetService;
    private final MarketListingService marketListingService;
    private final MarketListingCacheService  marketListingCacheService;

    @Transactional
    public void purchase(Long buyerId, Long marketListingId) {

        Member buyer = memberService.findMember(buyerId);
        Wallet wallet = walletService.findWallet(buyerId);
        MarketListing marketListing = marketListingService.findMarketListing(marketListingId);

        marketListing.validateSelling();
        wallet.checkBalanceAvailable(marketListing.getTotalPrice());

        Order order = orderService.createOrder(buyer, marketListing);

        walletService.payForOrder(wallet, marketListing, order);
        pendingAssetService.createTradePendingAsset(marketListing, order, buyer);

        marketListing.updateStatus(MarketListingStatus.SOLD);
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "marketListingsFirstPage", allEntries = true),
            @CacheEvict(cacheNames = "marketListingItem", key = "'listing:' + #marketListingId")
    })
    public CreateOrderResponse purchaseV2(Long buyerId, Long marketListingId) {

        Member buyer = memberService.findMember(buyerId);
        Wallet wallet = walletService.findWallet(buyerId);
        MarketListing marketListing = marketListingService.findMarketListing(marketListingId);

        marketListing.validateSelling();
        wallet.checkBalanceAvailable(marketListing.getTotalPrice());

        Order order = orderService.createOrder(buyer, marketListing);

        walletService.payForOrder(wallet, marketListing, order);
        pendingAssetService.createTradePendingAsset(marketListing, order, buyer);

        marketListing.updateStatus(MarketListingStatus.SOLD);

        return CreateOrderResponse.create(order, marketListing.getItemName());
    }

    @Transactional
    public CreateOrderResponse purchaseV3(Long buyerId, Long marketListingId) {
        Member buyer = memberService.findMember(buyerId);
        Wallet wallet = walletService.findWallet(buyerId);
        MarketListing marketListing = marketListingService.findMarketListing(marketListingId);

        marketListing.validateSelling();
        wallet.checkBalanceAvailable(marketListing.getTotalPrice());

        Order order = orderService.createOrder(buyer, marketListing);

        walletService.payForOrder(wallet, marketListing, order);
        pendingAssetService.createTradePendingAsset(marketListing, order, buyer);

        marketListing.updateStatus(MarketListingStatus.SOLD);

        marketListingCacheService.deleteMarketListingFirstPage();
        marketListingCacheService.deleteMarketListingItem(marketListingId);

        return CreateOrderResponse.create(order, marketListing.getItemName());
    }
}
