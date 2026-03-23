package com.example.tradedemo.domain.coupon;

import com.example.tradedemo.domain.coupon.entity.CouponPolicy;
import com.example.tradedemo.domain.coupon.enums.IssueType;
import com.example.tradedemo.domain.coupon.repository.CouponPolicyRepository;
import com.example.tradedemo.domain.coupon.repository.MemberCouponRepository;
import com.example.tradedemo.domain.coupon.service.CouponService;
import com.example.tradedemo.domain.coupon.facade.CouponFacade;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CouponConcurrencyTest {
    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private MemberCouponRepository memberCouponRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WalletRepository walletRepository;

    private CouponPolicy couponPolicy;
    private List<Member> members;

    // 쿠폰의 총 개수
    private static final int TOTAL_QUANTITY = 100;
    // 유저의 수
    private static final int THREAD_COUNT = 150;

    @BeforeEach
    void setUp(){
        // 선착순 쿠폰 정책 생성
        couponPolicy = couponPolicyRepository.save(CouponPolicy.create(
                "테스트용 선착순 쿠폰",
                BigDecimal.valueOf(5000),
                IssueType.FIRST_COME,
                (long) TOTAL_QUANTITY,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(3),
                java.time.Duration.ofDays(3),
                java.time.Duration.ofDays(7)));

        // 테스트 유저 생성
        members = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            Member member = memberRepository.save(
                    Member.create("user" + i + "@test.com", "password", "유저" + i, MemberRole.USER));
            walletRepository.save(Wallet.create(member, BigDecimal.ZERO));
            members.add(member);
        }
    }

    @AfterEach
    void tearDown() {
        memberCouponRepository.deleteAllInBatch();
        couponPolicyRepository.deleteAllInBatch();
        walletRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    /**
     * 테스트 결과
     *  ========================================
     *  쿠폰 총 수량:        100
     *  동시 요청 수:        100
     *  실제 DB 발급 수:      29
     *  expendQuantity:    16
     *  ========================================
     *
     *  expected: 16L
     *  but was: 29L
     *  Expected :16L
     *  Actual   :29L
     *  -> DeadLock 문제도 함께 발생하고 있음
     */
    @Test
    @Disabled("Lock을 적용하지 않아 무조건 실패하는 테스트")
    @DisplayName("V2 분산락 적용 전 - 선착순 쿠폰 발급 동시성 테스트")
    void 선착순쿠폰_동시발급() throws InterruptedException {
        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        // CyclicBarrier: 100개의 스레드가 동시에 출발하도록 제어하는 역할
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Member member = members.get(i);
            executor.submit(() -> {
                try {
                    barrier.await();
                    couponService.issueFirstComeCoupon(couponPolicy.getId(), member);
                } catch (Exception ignored) {
                }
            });
        }

        executor.shutdown();

        executor.awaitTermination(10, TimeUnit.SECONDS);

        // then
        long issuedCount = memberCouponRepository.count();
        CouponPolicy updatedPolicy = couponPolicyRepository.findById(couponPolicy.getId()).orElseThrow();

        System.out.println("========================================");
        System.out.println("쿠폰 총 수량:        " + TOTAL_QUANTITY);
        System.out.println("동시 요청 수:        " + THREAD_COUNT);
        System.out.println("실제 DB 발급 수:      " + issuedCount);
        System.out.println("expendQuantity:    " + updatedPolicy.getExpendQuantity());
        System.out.println("========================================");

        assertThat(issuedCount).isEqualTo(updatedPolicy.getExpendQuantity());
    }


    /**
     * V2 테스트 결과
     *  ========================================
     *  쿠폰 총 수량:        100
     *  동시 요청 수:        150
     *  실제 DB 발급 수:      100
     *  expendQuantity:    100
     *  ========================================
     */
    @Test
    @DisplayName("V2 분산락 적용 - 선착순 쿠폰 발급 동시성 테스트")
    void 선착순쿠폰_동시발급_Redis_Lock_V2() throws InterruptedException {

        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Member member = members.get(i);
            executor.submit(() -> {
                try {
                    barrier.await();
                    couponService.issueFirstComeCouponV2(couponPolicy.getId(), member);
                } catch (Exception ignored) {
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);

        // then
        long issuedCount = memberCouponRepository.count();
        CouponPolicy updatedPolicy = couponPolicyRepository.findById(couponPolicy.getId()).orElseThrow();

        System.out.println("========================================");
        System.out.println("쿠폰 총 수량:        " + TOTAL_QUANTITY);
        System.out.println("동시 요청 수:        " + THREAD_COUNT);
        System.out.println("실제 DB 발급 수:      " + issuedCount);
        System.out.println("expendQuantity:    " + updatedPolicy.getExpendQuantity());
        System.out.println("========================================");

        // 선착순 100개, 150명 요청 시 100명 성공
        // 발급받은 사람 수와 쿠폰 정책이 발급된 수량이 일치해야함
        assertThat(issuedCount).isEqualTo(updatedPolicy.getExpendQuantity());
        assertThat(issuedCount).isEqualTo(TOTAL_QUANTITY);
    }

    /**
     * V3_1 테스트 결과
     *  ========================================
     *  쿠폰 총 수량:        100
     *  동시 요청 수:        150
     *  실제 DB 발급 수:      100
     *  expendQuantity:    100
     *  ========================================
     */
    @Test
    @DisplayName("V3_1 Lettuce 분산락 + AOP 적용 - 선착순 쿠폰 발급 동시성 테스트")
    void 선착순쿠폰_동시발급_Redis_Lock_V3_1() throws InterruptedException {

        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Member member = members.get(i);
            executor.submit(() -> {
                try {
                    barrier.await();
                    couponService.issueFirstComeCouponV3_1(couponPolicy.getId(), member);
                } catch (Exception ignored) {
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);

        // then
        long issuedCount = memberCouponRepository.count();
        CouponPolicy updatedPolicy = couponPolicyRepository.findById(couponPolicy.getId()).orElseThrow();

        System.out.println("========================================");
        System.out.println("쿠폰 총 수량:        " + TOTAL_QUANTITY);
        System.out.println("동시 요청 수:        " + THREAD_COUNT);
        System.out.println("실제 DB 발급 수:      " + issuedCount);
        System.out.println("expendQuantity:    " + updatedPolicy.getExpendQuantity());
        System.out.println("========================================");

        // 선착순 100개, 150명 요청 시 100명 성공
        // 발급받은 사람 수와 쿠폰 정책이 발급된 수량이 일치해야함
        assertThat(issuedCount).isEqualTo(updatedPolicy.getExpendQuantity());
        assertThat(issuedCount).isEqualTo(TOTAL_QUANTITY);
    }

    /**
     * V3_2 테스트 결과
     *  ========================================
     *  쿠폰 총 수량:        100
     *  동시 요청 수:        150
     *  실제 DB 발급 수:      100
     *  expendQuantity:    100
     *  ========================================
     */
    @Test
    @DisplayName("V3_2 Redisson 분산락 + AOP 적용 - 선착순 쿠폰 발급 동시성 테스트")
    void 선착순쿠폰_동시발급_Redis_Lock_V3_2() throws InterruptedException {

        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Member member = members.get(i);
            executor.submit(() -> {
                try {
                    barrier.await();
                    couponService.issueFirstComeCouponV3_2(couponPolicy.getId(), member);
                } catch (Exception ignored) {
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);

        // then
        long issuedCount = memberCouponRepository.count();
        CouponPolicy updatedPolicy = couponPolicyRepository.findById(couponPolicy.getId()).orElseThrow();

        System.out.println("========================================");
        System.out.println("쿠폰 총 수량:        " + TOTAL_QUANTITY);
        System.out.println("동시 요청 수:        " + THREAD_COUNT);
        System.out.println("실제 DB 발급 수:      " + issuedCount);
        System.out.println("expendQuantity:    " + updatedPolicy.getExpendQuantity());
        System.out.println("========================================");

        // 선착순 100개, 150명 요청 시 100명 성공
        // 발급받은 사람 수와 쿠폰 정책이 발급된 수량이 일치해야함
        assertThat(issuedCount).isEqualTo(updatedPolicy.getExpendQuantity());
        assertThat(issuedCount).isEqualTo(TOTAL_QUANTITY);
    }

    /**
     * V3_3 테스트 결과
     *  ========================================
     *  쿠폰 총 수량:        100
     *  동시 요청 수:        150
     *  실제 DB 발급 수:      100
     *  expendQuantity:    100
     *  ========================================
     */
    @Test
    @DisplayName("V3_3 Redisson 분산락 + AOP 적용 - 선착순 쿠폰 발급 동시성 테스트 + 쿠폰 다 떨어짐 cache")
    void 선착순쿠폰_동시발급_V3_3() throws InterruptedException {

        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Member member = members.get(i);
            executor.submit(() -> {
                try {
                    barrier.await();
                    couponFacade.issueFirstComeCouponV3_3(couponPolicy.getId(), member);
                } catch (Exception ignored) {
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(100, TimeUnit.SECONDS);

        // then
        long issuedCount = memberCouponRepository.count();
        CouponPolicy updatedPolicy = couponPolicyRepository.findById(couponPolicy.getId()).orElseThrow();

        System.out.println("========================================");
        System.out.println("쿠폰 총 수량:        " + TOTAL_QUANTITY);
        System.out.println("동시 요청 수:        " + THREAD_COUNT);
        System.out.println("실제 DB 발급 수:      " + issuedCount);
        System.out.println("expendQuantity:    " + updatedPolicy.getExpendQuantity());
        System.out.println("========================================");

        // 선착순 100개, 150명 요청 시 100명 성공
        // 발급받은 사람 수와 쿠폰 정책이 발급된 수량이 일치해야함
        assertThat(issuedCount).isEqualTo(updatedPolicy.getExpendQuantity());
        assertThat(issuedCount).isEqualTo(TOTAL_QUANTITY);
    }

}
