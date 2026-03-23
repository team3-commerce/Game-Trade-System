package com.example.tradedemo.domain.order.facade;

import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.enums.ItemType;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.order.repository.OrderRepository;
import com.example.tradedemo.domain.pending.repository.PendingAssetRepository;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.repository.WalletHistoryRepository;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 상품 구매 V3 동시성 테스트
 * 대상: OrderFacade.purchaseV3 (@RedissonLock + Redis 캐시 삭제)
 */
@SpringBootTest
class OrderFacadeTest {

    @Autowired private OrderFacade orderFacade;

    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberItemRepository memberItemRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private MarketListingRepository marketListingRepository;
    @Autowired private PendingAssetRepository pendingAssetRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private WalletHistoryRepository walletHistoryRepository;
    @Autowired private OrderRepository orderRepository;

    private Member seller;
    private Member buyer;
    private Item item;
    private MemberItem sellerItem;

    @BeforeEach
    void setUp() {
        tearDown();

        item = Item.create("전설의 검", ItemType.EQUIPMENT);
        itemRepository.save(item);

        seller = Member.create("seller@test.com", "pw", "seller", MemberRole.USER);
        memberRepository.save(seller);

        sellerItem = MemberItem.create(seller, item, LocalDateTime.now(), 10L);
        memberItemRepository.save(sellerItem);

        buyer = Member.create("buyer@test.com", "pw", "buyer", MemberRole.USER);
        memberRepository.save(buyer);

        walletRepository.save(Wallet.create(buyer, BigDecimal.valueOf(10_000)));
    }

    @AfterEach
    void tearDown() {
        walletHistoryRepository.deleteAll();
        pendingAssetRepository.deleteAll();
        orderRepository.deleteAll();        // orders → market_listings 외래키 때문에 먼저 삭제
        marketListingRepository.deleteAll();
        memberItemRepository.deleteAll();
        walletRepository.deleteAll();
        memberRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("V3 Redisson 락 — 두 구매자 동시 구매 시 1건만 성공")
    void 구매_RedissonLock_V3_동시2회_1건만성공() throws InterruptedException {
        // given
        Member buyer2 = Member.create("buyer2@test.com", "pw", "buyer2", MemberRole.USER);
        memberRepository.save(buyer2);
        walletRepository.save(Wallet.create(buyer2, BigDecimal.valueOf(10_000)));

        MarketListing listing = MarketListing.create(
                item.getName(),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(500),
                1L,
                Duration.ofDays(7),
                sellerItem,
                seller);
        marketListingRepository.save(listing);

        // when
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Long[] buyerIds = { buyer.getId(), buyer2.getId() };
        for (int i = 0; i < threadCount; i++) {
            final Long buyerId = buyerIds[i];
            executor.submit(() -> {
                try {
                    orderFacade.purchaseV3(buyerId, listing.getId());
                } catch (Exception e) {
                    System.out.println("Exception caught: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // then
        MarketListing saved = marketListingRepository.findById(listing.getId()).orElseThrow();
        assertEquals(MarketListingStatus.SOLD, saved.getStatus(),
                "매물 상태는 SOLD 이어야 한다");

        long pendingCount = pendingAssetRepository.findAll().stream()
                .filter(a -> a.getMarketListing().getId().equals(listing.getId()))
                .count();
        // 구매 성공 1건 → PendingAsset 2개(판매자 수익 + 구매자 아이템)
        assertEquals(2, pendingCount,
                "PendingAsset은 정확히 2개(판매자 수익 + 구매자 아이템)여야 한다");

        System.out.println("========================================");
        System.out.println("구매 V3 Redisson 락 - 두 구매자 동시 구매");
        System.out.println("최종 매물 상태:       " + saved.getStatus());
        System.out.println("PendingAsset 생성 수: " + pendingCount);
        System.out.println("예상 PendingAsset:    2");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("V3 Redisson 락 — 동일 구매자 동시 2번 클릭 시 중복 구매 방지")
    void 구매_RedissonLock_V3_동일구매자_중복클릭() throws InterruptedException {
        // given
        MarketListing listing = MarketListing.create(
                item.getName(),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(500),
                1L,
                Duration.ofDays(7),
                sellerItem,
                seller);
        marketListingRepository.save(listing);

        Wallet buyerWallet = walletRepository.findByMemberId(buyer.getId()).orElseThrow();
        BigDecimal initialBalance = buyerWallet.getBalance();

        // when
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    orderFacade.purchaseV3(buyer.getId(), listing.getId());
                } catch (Exception e) {
                    System.out.println("Exception caught: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // then
        Wallet walletAfter = walletRepository.findByMemberId(buyer.getId()).orElseThrow();
        BigDecimal expectedBalance = initialBalance.subtract(BigDecimal.valueOf(500));

        assertEquals(0, expectedBalance.compareTo(walletAfter.getBalance()),
                "지갑 잔액은 단 한 번만 차감되어야 한다");

        System.out.println("========================================");
        System.out.println("구매 V3 Redisson 락 - 동일 구매자 중복 클릭");
        System.out.println("동시 요청 수: " + threadCount + " (500원 구매 2번 클릭)");
        System.out.println("초기 잔액:    " + initialBalance);
        System.out.println("최종 잔액:    " + walletAfter.getBalance());
        System.out.println("예상 잔액:    " + expectedBalance);
        System.out.println("========================================");
    }
}