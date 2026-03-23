package com.example.tradedemo.domain.pending.service;

import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.enums.ItemType;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.enums.PendingType;
import com.example.tradedemo.domain.pending.enums.Type;
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
 * 자산 수령하기 동시성 테스트
 * 대상:
 *  - PendingAssetService.claimPendingAssetV2 (@RedisLock + @CacheEvict)
 *  - PendingAssetService.claimPendingAssetV3 (@RedissonLock + Redis 캐시 삭제)
 */
@SpringBootTest
class PendingAssetServiceTest {

    @Autowired private PendingAssetService pendingAssetService;

    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberItemRepository memberItemRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private MarketListingRepository marketListingRepository;
    @Autowired private PendingAssetRepository pendingAssetRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private WalletHistoryRepository walletHistoryRepository;

    private Member seller;
    private Item item;
    private MemberItem sellerItem;
    private MarketListing listing;

    @BeforeEach
    void setUp() {
        tearDown();

        item = Item.create("전설의 검", ItemType.EQUIPMENT);
        itemRepository.save(item);

        seller = Member.create("seller@test.com", "pw", "seller", MemberRole.USER);
        memberRepository.save(seller);

        // 판매자 지갑 (초기 잔액 0)
        walletRepository.save(Wallet.create(seller, BigDecimal.ZERO));

        sellerItem = MemberItem.create(seller, item, LocalDateTime.now(), 10L);
        memberItemRepository.save(sellerItem);

        listing = MarketListing.create(
                item.getName(),
                BigDecimal.valueOf(300),
                BigDecimal.valueOf(300),
                1L,
                Duration.ofDays(7),
                sellerItem,
                seller);
        marketListingRepository.save(listing);
    }

    @AfterEach
    void tearDown() {
        walletHistoryRepository.deleteAll();
        pendingAssetRepository.deleteAll();
        marketListingRepository.deleteAll();
        memberItemRepository.deleteAll();
        walletRepository.deleteAll();
        memberRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("V2 RedisLock + CacheEvict — 동시 2번 클릭 시 중복 수령 방지")
    void 수령하기_RedisLock_V2_중복수령방지() throws InterruptedException {
        // given
        PendingAsset pending = PendingAsset.create(
                PendingType.SALE_SUCCESS,
                Type.MONEY,
                BigDecimal.valueOf(300),
                0L,
                false,
                null,
                LocalDateTime.now().plusDays(1),
                listing,
                null,
                seller
        );
        pendingAssetRepository.save(pending);

        // when
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    pendingAssetService.claimPendingAssetV2(seller.getId(), pending.getId());
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
        PendingAsset assetAfter = pendingAssetRepository.findById(pending.getId()).orElseThrow();
        assertTrue(assetAfter.getIsClaimed(), "PendingAsset은 수령 처리되어야 한다");

        Wallet walletAfter = walletRepository.findByMemberId(seller.getId()).orElseThrow();
        assertEquals(0, BigDecimal.valueOf(300).compareTo(walletAfter.getBalance()),
                "지갑 잔액은 300이어야 한다 (중복 수령 방지)");

        System.out.println("========================================");
        System.out.println("수령하기 V2 RedisLock + CacheEvict");
        System.out.println("동시 요청 수: " + threadCount + " (300원 수령 2번 클릭)");
        System.out.println("최종 잔액:    " + walletAfter.getBalance());
        System.out.println("예상 잔액:    300");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("V3 RedissonLock + Redis 캐시 삭제 — 동시 2번 클릭 시 중복 수령 방지")
    void 수령하기_RedissonLock_V3_중복수령방지() throws InterruptedException {
        // given
        PendingAsset pending = PendingAsset.create(
                PendingType.SALE_SUCCESS,
                Type.MONEY,
                BigDecimal.valueOf(300),
                0L,
                false,
                null,
                LocalDateTime.now().plusDays(1),
                listing,
                null,
                seller
        );
        pendingAssetRepository.save(pending);

        // when
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    pendingAssetService.claimPendingAssetV3(seller.getId(), pending.getId());
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
        PendingAsset assetAfter = pendingAssetRepository.findById(pending.getId()).orElseThrow();
        assertTrue(assetAfter.getIsClaimed(), "PendingAsset은 수령 처리되어야 한다");

        Wallet walletAfter = walletRepository.findByMemberId(seller.getId()).orElseThrow();
        assertEquals(0, BigDecimal.valueOf(300).compareTo(walletAfter.getBalance()),
                "지갑 잔액은 300이어야 한다 (중복 수령 방지)");

        System.out.println("========================================");
        System.out.println("수령하기 V3 RedissonLock + Redis 캐시 삭제");
        System.out.println("동시 요청 수: " + threadCount + " (300원 수령 2번 클릭)");
        System.out.println("최종 잔액:    " + walletAfter.getBalance());
        System.out.println("예상 잔액:    300");
        System.out.println("========================================");
    }
}