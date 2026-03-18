package com.example.tradedemo.domain.coupon.service;

import com.example.tradedemo.common.annotation.RedisLock;
import com.example.tradedemo.common.annotation.RedissonLock;
import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.constants.CouponDuration;
import com.example.tradedemo.domain.coupon.dto.*;
import com.example.tradedemo.domain.coupon.entity.CouponHistory;
import com.example.tradedemo.domain.coupon.entity.CouponPolicy;
import com.example.tradedemo.domain.coupon.entity.MemberCoupon;
import com.example.tradedemo.domain.coupon.enums.CouponStatus;
import com.example.tradedemo.domain.coupon.enums.IssueType;
import com.example.tradedemo.domain.coupon.exception.CouponExpiredException;
import com.example.tradedemo.domain.coupon.repository.CouponHistoryRepository;
import com.example.tradedemo.domain.coupon.repository.CouponPolicyRepository;
import com.example.tradedemo.domain.coupon.repository.MemberCouponRepository;
import com.example.tradedemo.domain.members.entity.Member;

import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponPolicyRepository couponPolicyRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CouponHistoryRepository couponHistoryRepository;
    private final LockService lockService;
    private final CouponIssueService couponIssueService;

    @Transactional
    public CreateCouponPolicyResponse createCouponPolicy(@Valid CreateCouponPolicyRequest request) {
        // 정책 이름 중복 검사
        if (couponPolicyRepository.existsByName(request.getName())) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_POLICY_DUPLICATE_NAME);
        }

        // FIRST_COME 이면 totalQuantity 필수
        if (request.getIssueType() == IssueType.FIRST_COME && request.getTotalQuantity() == null) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_POLICY_FIRST_COME_QUANTITY_REQUIRED);
        }

        // AUTO_SIGNUP 은 하나만 존재할 수 있음
        if (request.getIssueType() == IssueType.AUTO_SIGNUP
                && couponPolicyRepository.existsByIssueType(IssueType.AUTO_SIGNUP)) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_POLICY_AUTO_SIGNUP_ALREADY_EXISTS);
        }

        // 정책 시작일 = 생성 시점
        LocalDateTime policyStartedAt = LocalDateTime.now();

        // IssueType 에 따라 상수에서 Duration 자동 선택
        Duration policyDuration = CouponDuration.getPolicyDuration(request.getIssueType(), request.getPolicyDuration());
        Duration couponDuration = CouponDuration.getCouponDuration(request.getIssueType(), request.getCouponDuration());

        // 정책 만료일 = 시작일 + policyDuration (AUTO_SIGNUP 이면 null)
        LocalDateTime policyExpiredAt = policyDuration != null ? policyStartedAt.plus(policyDuration) : null;

        CouponPolicy couponPolicy = CouponPolicy.create(
                request.getName(),
                request.getMoneyAmount(),
                request.getIssueType(),
                request.getTotalQuantity(),
                policyStartedAt,
                policyExpiredAt,
                policyDuration,
                couponDuration);

        CouponPolicy savedPolicy = couponPolicyRepository.save(couponPolicy);

        return CreateCouponPolicyResponse.from(savedPolicy);
    }

    @Transactional
    @CacheEvict(value = "couponPolicies", allEntries = true)
    public CreateCouponPolicyResponse createCouponPolicyV2(@Valid CreateCouponPolicyRequest request) {
        // 정책 이름 중복 검사
        if (couponPolicyRepository.existsByName(request.getName())) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_POLICY_DUPLICATE_NAME);
        }

        // FIRST_COME 이면 totalQuantity 필수
        if (request.getIssueType() == IssueType.FIRST_COME && request.getTotalQuantity() == null) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_POLICY_FIRST_COME_QUANTITY_REQUIRED);
        }

        // AUTO_SIGNUP 은 하나만 존재할 수 있음
        if (request.getIssueType() == IssueType.AUTO_SIGNUP
                && couponPolicyRepository.existsByIssueType(IssueType.AUTO_SIGNUP)) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_POLICY_AUTO_SIGNUP_ALREADY_EXISTS);
        }

        LocalDateTime policyStartedAt = LocalDateTime.now();

        Duration policyDuration = CouponDuration.getPolicyDuration(request.getIssueType(), request.getPolicyDuration());
        Duration couponDuration = CouponDuration.getCouponDuration(request.getIssueType(), request.getCouponDuration());

        LocalDateTime policyExpiredAt = policyDuration != null ? policyStartedAt.plus(policyDuration) : null;

        CouponPolicy couponPolicy = CouponPolicy.create(
                request.getName(),
                request.getMoneyAmount(),
                request.getIssueType(),
                request.getTotalQuantity(),
                policyStartedAt,
                policyExpiredAt,
                policyDuration,
                couponDuration);

        CouponPolicy savedPolicy = couponPolicyRepository.save(couponPolicy);

        return CreateCouponPolicyResponse.from(savedPolicy);
    }

    @Transactional
    public void autoSignupCoupon(Member member) {

        // AUTO_SIGNUP 정책 없으면 회원가입 시 쿠폰 미발급
        // AUTO_SIGNUP 정책 있으면 회원가입 시 쿠폰 발급
        CouponPolicy couponPolicy =
                couponPolicyRepository.findByIssueType(IssueType.AUTO_SIGNUP).orElse(null);

        if (couponPolicy == null) {
            return;
        }

        // 중복 발급 방지
        if (memberCouponRepository.existsByMemberAndCouponPolicy(member, couponPolicy)) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_ALREADY_ISSUED);
        }

        // 발급 시점
        LocalDateTime issuedAt = LocalDateTime.now();

        // couponDuration이 null 이면 만료 없음
        LocalDateTime expiredAt =
                couponPolicy.getCouponDuration() != null ? issuedAt.plus(couponPolicy.getCouponDuration()) : null;

        MemberCoupon memberCoupon = MemberCoupon.create(member, couponPolicy, issuedAt, expiredAt);
        memberCouponRepository.save(memberCoupon);

        couponPolicy.increaseExpendQuantity();
    }

    @Transactional(readOnly = true)
    public PageResponse<SearchAllCouponPolicyResponse> searchAllCouponPolicies(
            String sortCreatedAt, String issueType, Pageable pageable) {
        return couponPolicyRepository.getAllCouponPolicy(sortCreatedAt, issueType, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "couponPolicies",
            key = "'page:' + #pageable.pageNumber + ':sort:' + #sortCreatedAt + ':type:' + #issueType",
            unless = "#result.content.isEmpty()")
    public PageResponse<SearchAllCouponPolicyResponse> searchAllCouponPoliciesV2(
            String sortCreatedAt, String issueType, Pageable pageable) {
        return couponPolicyRepository.getAllCouponPolicy(sortCreatedAt, issueType, pageable);
    }


    @Transactional(readOnly = true)
    public PageResponse<SearchAllMemberCouponResponse> getAllMemberCoupon(Long memberId, String status, Pageable pageable) {
        return memberCouponRepository.findAllMemberCouponByMemberId(memberId, status, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "memberCoupons",
            key = "'member:' + #memberId + ':page:' + #pageable.pageNumber + ':status:' + #status",
            unless = "#result.content.isEmpty()")
    public PageResponse<SearchAllMemberCouponResponse> getAllMemberCouponV2(Long memberId, String status, Pageable pageable) {
        return memberCouponRepository.findAllMemberCouponByMemberId(memberId, status, pageable);
    }

    @Transactional(readOnly = true)
    public SearchAllMemberCouponResponse getMemberCoupon(Long memberId, Long couponId) {
        return memberCouponRepository
                .findMemberCouponByMemberIdAndMemberCouponId(memberId, couponId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_COUPON_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "memberCoupons",
            key = "'member:' + #memberId + ':coupon:' + #couponId")
    public SearchAllMemberCouponResponse getMemberCouponV2(Long memberId, Long couponId) {
        return memberCouponRepository
                .findMemberCouponByMemberIdAndMemberCouponId(memberId, couponId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_COUPON_NOT_FOUND));
    }

    @Transactional
    public void issueFirstComeCoupon(Long couponPolicyId, Member member) {
        // FIRST_COME 정책 조회
        CouponPolicy couponPolicy = couponPolicyRepository
                .findByIdAndIssueType(couponPolicyId, IssueType.FIRST_COME)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_COUPON_POLICY_NOT_FOUND));

        // 발급 가능 여부 체크
        if (!couponPolicy.isIssuable()) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_POLICY_SOLD_OUT);
        }

        // 중복 발급 방지
        if (memberCouponRepository.existsByMemberAndCouponPolicy(member, couponPolicy)) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_ALREADY_ISSUED);
        }

        // 발급 시점
        LocalDateTime issuedAt = LocalDateTime.now();

        // 쿠폰 만료일 = 발급 시점 + couponDuration
        LocalDateTime expiredAt = issuedAt.plus(couponPolicy.getCouponDuration());

        memberCouponRepository.save(MemberCoupon.create(member, couponPolicy, issuedAt, expiredAt));

        couponPolicy.increaseExpendQuantity();
    }


    @Caching(evict = {
            @CacheEvict(value = "memberCoupons", allEntries = true),
            @CacheEvict(value = "couponPolicies", allEntries = true)
    })
    public void issueFirstComeCouponV2(Long couponPolicyId, Member member) {
        String lockKey = lockService.buildLockKey(couponPolicyId);
        String lockValue = lockService.acquireLock(lockKey); // 락 획득

        try {
            // DeadLock 발생 -> 기존 로직을 트랜잭션 분리
            couponIssueService.issueFirstComeCouponV2(couponPolicyId, member);
        } finally {
            lockService.releaseLock(lockKey, lockValue); // 락 해제
        }

    }

    /**
     * Redis Lettuce + @RedisLock AOP 적용
     */
    @RedisLock(key = "'lock:coupon:' + #couponPolicyId")
    @Transactional
    public void issueFirstComeCouponV3_1(Long couponPolicyId, Member member) {

        // FIRST_COME 정책 조회
        CouponPolicy couponPolicy = couponPolicyRepository
                .findByIdAndIssueType(couponPolicyId, IssueType.FIRST_COME)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_COUPON_POLICY_NOT_FOUND));

        // 발급 가능 여부 체크
        if (!couponPolicy.isIssuable()) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_POLICY_SOLD_OUT);
        }

        // 중복 발급 방지
        if (memberCouponRepository.existsByMemberAndCouponPolicy(member, couponPolicy)) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_ALREADY_ISSUED);
        }

        // 발급 시점
        LocalDateTime issuedAt = LocalDateTime.now();

        // 쿠폰 만료일 = 발급 시점 + couponDuration
        LocalDateTime expiredAt = issuedAt.plus(couponPolicy.getCouponDuration());

        memberCouponRepository.save(MemberCoupon.create(member, couponPolicy, issuedAt, expiredAt));

        couponPolicy.increaseExpendQuantity();
    }

    /**
     * Redis Redisson + @RedissonLock AOP 적용
     */
    @RedissonLock(key = "'lock:coupon:' + #couponPolicyId")
    @Transactional
    public void issueFirstComeCouponV3_2(Long couponPolicyId, Member member) {

        // FIRST_COME 정책 조회
        CouponPolicy couponPolicy = couponPolicyRepository
                .findByIdAndIssueType(couponPolicyId, IssueType.FIRST_COME)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_COUPON_POLICY_NOT_FOUND));

        // 발급 가능 여부 체크
        if (!couponPolicy.isIssuable()) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_POLICY_SOLD_OUT);
        }

        // 중복 발급 방지
        if (memberCouponRepository.existsByMemberAndCouponPolicy(member, couponPolicy)) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_ALREADY_ISSUED);
        }

        // 발급 시점
        LocalDateTime issuedAt = LocalDateTime.now();

        // 쿠폰 만료일 = 발급 시점 + couponDuration
        LocalDateTime expiredAt = issuedAt.plus(couponPolicy.getCouponDuration());

        memberCouponRepository.save(MemberCoupon.create(member, couponPolicy, issuedAt, expiredAt));

        couponPolicy.increaseExpendQuantity();
    }

    public CouponHistory useCoupon(Long memberId, Long memberCouponId, Member member) {
        // 본인 쿠폰인지 조회
        MemberCoupon memberCoupon = memberCouponRepository
                .findMemberCouponForUse(memberId, memberCouponId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_COUPON_NOT_FOUND));

        // 만료일이 지난 경우 EXPIRED 처리
        if (memberCoupon.isExpired()) {
            memberCoupon.updateExpireStatus();
            couponHistoryRepository.save(CouponHistory.createExpired(member, memberCoupon));
            log.info("만료 처리 및 기록 추가 완료");
        }
        if (memberCoupon.getStatus() == CouponStatus.EXPIRED) {
            throw new CouponExpiredException();
        }

        // 사용 가능 여부 체크
        if (memberCoupon.getStatus() != CouponStatus.UNUSED) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_NOT_USABLE);
        }

        memberCoupon.updateUsedStatus();
        return couponHistoryRepository.save(CouponHistory.create(member, memberCoupon));
    }

    @Transactional(readOnly = true)
    public PageResponse<SearchAllCouponHistoryResponse> getAllCouponHistory(
            Long memberId, String status, String sortCreatedAt, Pageable pageable) {
        return couponHistoryRepository.findAllCouponHistoryByMemberId(memberId, status, sortCreatedAt, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "couponHistories",
            key = "'member:' + #memberId + ':page:' + #pageable.pageNumber + ':status:' + #status + ':sort:' + #sortCreatedAt",
            unless = "#result.content.isEmpty()")
    public PageResponse<SearchAllCouponHistoryResponse> getAllCouponHistoryV2(
            Long memberId, String status, String sortCreatedAt, Pageable pageable) {
        return couponHistoryRepository.findAllCouponHistoryByMemberId(memberId, status, sortCreatedAt, pageable);
    }
}
