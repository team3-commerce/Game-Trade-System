package com.example.tradedemo.domain.order.facade;


import static com.example.tradedemo.domain.marketlistings.consts.MarketListingConsts.*;

import com.example.tradedemo.common.annotation.RedissonLock;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.service.MarketListingCacheService;
import com.example.tradedemo.domain.marketlistings.service.MarketListingService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.service.MemberService;
import com.example.tradedemo.domain.order.dto.CreateOrderResponse;
import com.example.tradedemo.domain.order.entity.Order;
import com.example.tradedemo.domain.order.service.OrderService;
import com.example.tradedemo.domain.pending.service.PendingAssetService;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.facade.WalletFacade;
import com.example.tradedemo.domain.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    
    private final OrderService orderService;
    private final WalletService walletService;
    private final WalletFacade walletFacade;
    private final MemberService memberService;
    private final PendingAssetService pendingAssetService;
    private final MarketListingService marketListingService;
    private final MarketListingCacheService marketListingCacheService;

    /**
     * 상품 구매 V1
     * @param buyerId
     * @param marketListingId
     */
    @Transactional
    public void purchase(Long buyerId, Long marketListingId) {

        Member buyer = memberService.findMember(buyerId);
        Wallet wallet = walletService.findWallet(buyerId);
        MarketListing marketListing = marketListingService.findMarketListing(marketListingId);

        marketListing.validateSelling();
        wallet.checkBalanceAvailable(marketListing.getTotalPrice());

        Order order = orderService.createOrder(buyer, marketListing);

        walletFacade.payForOrder(wallet, marketListing, order);
        pendingAssetService.createTradePendingAsset(marketListing, order, buyer);

        marketListing.updateStatus(MarketListingStatus.SOLD);
    }

    /**
     * 상품 구매 V2 - 로컬 캐시 삭제
     * @param buyerId
     * @param marketListingId
     * @return
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = MARKET_LISTINGS_FIRST_PAGE_CACHE_NAME, allEntries = true),
            @CacheEvict(cacheNames = MARKET_LISTING_ITEM_CACHE_NAME, key = "'listing:' + #marketListingId")
    })
    public CreateOrderResponse purchaseV2(Long buyerId, Long marketListingId) {

        Member buyer = memberService.findMember(buyerId);
        Wallet wallet = walletService.findWallet(buyerId);
        MarketListing marketListing = marketListingService.findMarketListing(marketListingId);

        marketListing.validateSelling();
        wallet.checkBalanceAvailable(marketListing.getTotalPrice());

        Order order = orderService.createOrder(buyer, marketListing);

        walletFacade.payForOrder(wallet, marketListing, order);
        pendingAssetService.createTradePendingAsset(marketListing, order, buyer);

        marketListing.updateStatus(MarketListingStatus.SOLD);

        return CreateOrderResponse.create(order, marketListing.getItemName());
    }

    /**
     * 상품 구매 - Redis Redisson + @RedissonLock AOP 적용
     * Redis 캐시 삭제 : 거래소 조회 캐시
     * @param buyerId
     * @param marketListingId
     * @return
     */
    @RedissonLock(key = "'" + MARKET_LISTING_ID_LOCK_PREFIX + "' + #marketListingId")
    @Transactional
    public CreateOrderResponse purchaseV3(Long buyerId, Long marketListingId) {

        Member buyer = memberService.findMember(buyerId);
        Wallet wallet = walletService.findWallet(buyerId);
        MarketListing marketListing = marketListingService.findMarketListing(marketListingId);

        marketListing.validateSelling();
        wallet.checkBalanceAvailable(marketListing.getTotalPrice());

        Order order = orderService.createOrder(buyer, marketListing);

        walletFacade.payForOrder(wallet, marketListing, order);
        pendingAssetService.createTradePendingAsset(marketListing, order, buyer);

        marketListing.updateStatus(MarketListingStatus.SOLD);

        /**
         * Redis 캐시 삭제
         * 거래소 조회 기록 캐시 삭제
         */
        marketListingCacheService.deleteMarketListingItem(marketListingId);
        marketListingCacheService.deleteMarketListingFirstPage();

        return CreateOrderResponse.create(order, marketListing.getItemName());
    }
}
