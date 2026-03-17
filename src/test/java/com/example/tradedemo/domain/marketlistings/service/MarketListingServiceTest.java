package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.enums.ItemType;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import com.example.tradedemo.domain.marketlistings.dto.CreateMarketListingRequest;
import com.example.tradedemo.domain.marketlistings.enums.SalesDurations;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MarketListingServiceTest {

    @Autowired
    private MarketListingService marketListingService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberItemRepository memberItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Long memberId;
    private Long memberItemId;

    // 기본 설정
    @BeforeEach
    void setUp() {
        Member member = Member.create(
                "seller@test.com",
                "1234",
                "seller",
                MemberRole.USER
        );

        memberRepository.save(member);

        Item item = Item.create(
                "검",
                ItemType.EQUIPMENT
        );

        itemRepository.save(item);

        // 인벤토리 : 수량 10개
        MemberItem memberItem = MemberItem.create(
                member,
                item,
                LocalDateTime.now(),
                10L
        );

        memberItemRepository.save(memberItem);

        memberId = member.getId();
        memberItemId = memberItem.getId();
    }

    @Test
    @DisplayName("상품 등록 동시성 테스트 - 재고 10개를 두 번 등록")
    void 상품_등록_테스트_2번_연속_클릭() throws InterruptedException {

        int threadCount = 2; // 실행 횟수

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        CreateMarketListingRequest request = new CreateMarketListingRequest(
                memberItemId,
                BigDecimal.valueOf(1000),
                10L,
                SalesDurations.HOURS_12
        );

        for (int i = 0; i < threadCount; i++) {

            executorService.submit(() -> {

                try {

                    startLatch.await(); // 동시에 시작

                    marketListingService.create(memberId, request);

                    successCount.incrementAndGet();

                } catch (Exception e) {

                    failCount.incrementAndGet();
                    System.out.println("에러 발생: " + e.getClass().getSimpleName() + " / " + e.getMessage());

                } finally {
                    endLatch.countDown();
                }

            });
        }

        // 모든 스레드 동시에 시작
        startLatch.countDown();

        // 모든 스레드 종료 대기
        endLatch.await();

        MemberItem memberItem = memberItemRepository.findById(memberItemId).orElseThrow();

        System.out.println("===== 테스트 결과 =====");
        System.out.println("요청 횟수: " + threadCount);
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("실패 횟수: " + failCount.get());
        System.out.println("최종 재고: " + memberItem.getQuantity());
    }
    /*

    ===== 비관적 락 수정 후 테스트 결과 =====
    요청 횟수: 2
    성공 횟수: 1
    실패 횟수: 1
    최종 재고: 0
     */
}