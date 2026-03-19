package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.enums.ItemType;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import com.example.tradedemo.domain.marketlistings.dto.CreateMarketListingRequest;
import com.example.tradedemo.domain.marketlistings.enums.SalesDurations; // 프로젝트 enum에 맞게 수정
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.pending.repository.PendingAssetRepository;
import com.example.tradedemo.domain.wallet.repository.WalletHistoryRepository;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 상품 등록 동시성 테스트
 * 대상:
 *  - MarketListingService.createV4 (@RedisLock + Redis 캐시 삭제)
 *  - MarketListingService.createV5 (@RedissonLock + Redis 캐시 삭제)
 *
 * 검증 포인트:
 *  초기 재고 10개인 아이템을 수량 6으로 동시에 2번 등록 요청.
 *  첫 번째 성공 후 재고는 4개 → 두 번째 요청은 4 < 6 이므로 실패해야 한다.
 *  → 등록된 MarketListing 은 1건, 재고는 음수가 되어선 안 된다.
 */
@SpringBootTest
class MarketListingServiceTest {

    @Autowired private MarketListingService marketListingService;

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

    @BeforeEach
    void setUp() {
        tearDown();

        item = Item.create("전설의 검", ItemType.EQUIPMENT);
        itemRepository.save(item);

        seller = Member.create("seller@test.com", "pw", "seller", MemberRole.USER);
        memberRepository.save(seller);

        // 초기 재고 10
        sellerItem = MemberItem.create(seller, item, LocalDateTime.now(), 10L);
        memberItemRepository.save(sellerItem);
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
    @DisplayName("V4 RedisLock — 동시 2번 등록 시 재고 초과 차감 방지")
    void 상품등록_RedisLock_V4_동시2회_재고초과방지() throws InterruptedException {
        // given
        // 수량 6으로 2번 요청 → 한 번만 성공해야 함 (10 - 6 = 4, 두 번째 4 < 6 실패)
        CreateMarketListingRequest request = buildRequest(sellerItem.getId(), 6L, BigDecimal.valueOf(600));

        // when
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    marketListingService.createV4(seller.getId(), request);
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
        MemberItem itemAfter = memberItemRepository.findById(sellerItem.getId()).orElseThrow();
        assertTrue(itemAfter.getQuantity() >= 0, "재고는 음수가 되어선 안 된다");

        long listingCount = marketListingRepository.findAll().stream()
                .filter(l -> l.getMember().getId().equals(seller.getId()))
                .count();
        assertEquals(1, listingCount, "성공한 상품 등록은 정확히 1건이어야 한다");

        System.out.println("========================================");
        System.out.println("상품 등록 V4 RedisLock");
        System.out.println("동시 요청 수: 2 (수량 6 등록 2번 클릭)");
        System.out.println("남은 재고:    " + itemAfter.getQuantity());
        System.out.println("예상 재고:    4");
        System.out.println("등록 성공 건: " + listingCount);
        System.out.println("예상 성공 건: 1");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("V5 RedissonLock — 동시 2번 등록 시 재고 초과 차감 방지")
    void 상품등록_RedissonLock_V5_동시2회_재고초과방지() throws InterruptedException {
        // given
        CreateMarketListingRequest request = buildRequest(sellerItem.getId(), 6L, BigDecimal.valueOf(600));

        // when
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    marketListingService.createV5(seller.getId(), request);
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
        MemberItem itemAfter = memberItemRepository.findById(sellerItem.getId()).orElseThrow();
        assertTrue(itemAfter.getQuantity() >= 0, "재고는 음수가 되어선 안 된다");

        long listingCount = marketListingRepository.findAll().stream()
                .filter(l -> l.getMember().getId().equals(seller.getId()))
                .count();
        assertEquals(1, listingCount, "성공한 상품 등록은 정확히 1건이어야 한다");

        System.out.println("========================================");
        System.out.println("상품 등록 V5 RedissonLock");
        System.out.println("동시 요청 수: 2 (수량 6 등록 2번 클릭)");
        System.out.println("남은 재고:    " + itemAfter.getQuantity());
        System.out.println("예상 재고:    4");
        System.out.println("등록 성공 건: " + listingCount);
        System.out.println("예상 성공 건: 1");
        System.out.println("========================================");
    }

    /**
     * CreateMarketListingRequest 생성 헬퍼.
     * DTO에 빌더가 없으므로 setter 방식 사용.
     * DTO 필드명/setter가 다를 경우 실제 구조에 맞게 수정하세요.
     */
    private CreateMarketListingRequest buildRequest(Long memberItemId, Long quantity, BigDecimal totalPrice) {
        // 생성자 순서: memberItemId, totalPrice, quantity, salesDuration
        return new CreateMarketListingRequest(memberItemId, totalPrice, quantity, SalesDurations.HOURS_24);
    }
}