package com.example.tradedemo.domain.pending.service;

import com.example.tradedemo.common.exception.ServiceException;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PendingAssetServiceTest {

    @Autowired private PendingAssetService pendingAssetService;
    @Autowired private PendingAssetRepository pendingAssetRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private MemberItemRepository memberItemRepository;
    @Autowired private MarketListingRepository marketListingRepository;
    @Autowired private PlatformTransactionManager transactionManager;
    @Autowired private WalletHistoryRepository walletHistoryRepository;

    private PendingAsset pendingAsset;
    private Member buyer;

    @BeforeEach
    void setUp() {
        // 혹시 남은 데이터 먼저 정리
        tearDown();

        // 1. buyer
        buyer = Member.create(
                "buyer@test.com",
                "1234",
                "buyer",
                MemberRole.USER
        );
        memberRepository.save(buyer);
        walletRepository.save(Wallet.create(buyer, BigDecimal.ZERO));

        // 2. seller
        Member seller = Member.create(
                "seller@test.com",
                "1234",
                "seller",
                MemberRole.USER
        );
        memberRepository.save(seller);

        // 3. Item
        Item item = Item.create("검", ItemType.EQUIPMENT);
        itemRepository.save(item);

        // 4. MemberItem (seller 소유)
        MemberItem sellerItem = MemberItem.create(seller, item, LocalDateTime.now(), 10L);
        memberItemRepository.save(sellerItem);

        // 5. MarketListing
        MarketListing marketListing = MarketListing.create(
                item.getName(),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                1L,
                Duration.ofDays(7),
                sellerItem,
                seller
        );
        marketListingRepository.save(marketListing);

        // 6. PendingAsset
        pendingAsset = PendingAsset.create(
                PendingType.PURCHASE_SUCCESS,
                Type.MONEY,
                BigDecimal.valueOf(100),
                0L,
                false,
                null,
                LocalDateTime.now().plusDays(1),
                marketListing,
                null,
                buyer
        );
        pendingAssetRepository.save(pendingAsset);
    }

    @AfterEach
    void tearDown() {
        walletHistoryRepository.deleteAll();  // ← 추가
        pendingAssetRepository.deleteAll();
        marketListingRepository.deleteAll();
        memberItemRepository.deleteAll();
        walletRepository.deleteAll();
        memberRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("V1 비관적 락 - 수령하기 동시 2번 클릭")
    void 수령하기_비관적락_V1() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Runnable task = () -> {
            try {
                TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
                txTemplate.executeWithoutResult(status -> {
                    pendingAssetService.claimPendingAssetV2(buyer.getId(), pendingAsset.getId());
                });
            } catch (Exception e) {
                System.out.println("Exception caught: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < threadCount; i++) {
            executor.submit(task);
        }

        latch.await();
        executor.shutdown();

        PendingAsset assetFromDb = pendingAssetRepository.findById(pendingAsset.getId())
                .orElseThrow();
        assertTrue(assetFromDb.getIsClaimed(), "PendingAsset은 반드시 수령 처리되어야 함");

        Wallet walletFromDb = walletRepository.findByMemberId(buyer.getId())
                .orElseThrow();
        assertEquals(0, BigDecimal.valueOf(100).compareTo(walletFromDb.getBalance()),
                "Wallet 잔액은 100이어야 함 (중복 수령 방지)");
    }
    /*
    ========================================
    비관적 락
    동시 요청 수: 2(100원 수령하기 2번 누름)
    최종 잔액:    100.00
    예상 잔액:    100
    ========================================
    */

    @Test
    @DisplayName("V2 Redis 분산락 - 수령하기 동시 2번 클릭")
    void 수령하기_Redis_분산락_V2() throws InterruptedException {
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Runnable task = () -> {
            try {
                // V2는 내부에서 트랜잭션 처리하므로 TransactionTemplate 불필요
                pendingAssetService.claimPendingAssetV2(buyer.getId(), pendingAsset.getId());
            } catch (Exception e) {
                System.out.println("Exception caught: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < threadCount; i++) {
            executor.submit(task);
        }

        latch.await();
        executor.shutdown();

        PendingAsset assetFromDb = pendingAssetRepository.findById(pendingAsset.getId())
                .orElseThrow();
        assertTrue(assetFromDb.getIsClaimed(), "PendingAsset은 반드시 수령 처리되어야 함");

        Wallet walletFromDb = walletRepository.findByMemberId(buyer.getId())
                .orElseThrow();
        assertEquals(0, BigDecimal.valueOf(100).compareTo(walletFromDb.getBalance()),
                "Wallet 잔액은 100이어야 함 (중복 수령 방지)");
    }
    /*
    ========================================
    Redis 분산 락
    동시 요청 수: 2(100원 수령하기 2번 누름)
    최종 잔액:    100.00
    예상 잔액:    100
    ========================================
    */
}