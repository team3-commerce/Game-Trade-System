package com.example.tradedemo.domain.coupon.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.constants.CouponDuration;
import com.example.tradedemo.domain.coupon.dto.CreateCouponPolicyRequest;
import com.example.tradedemo.domain.coupon.dto.CreateCouponPolicyResponse;
import com.example.tradedemo.domain.coupon.dto.SearchAllCouponPolicyResponse;
import com.example.tradedemo.domain.coupon.dto.SearchAllMemberCouponResponse;
import com.example.tradedemo.domain.coupon.entity.CouponPolicy;
import com.example.tradedemo.domain.coupon.entity.MemberCoupon;
import com.example.tradedemo.domain.coupon.enums.IssueType;
import com.example.tradedemo.domain.coupon.repository.CouponHistoryRepository;
import com.example.tradedemo.domain.coupon.repository.CouponPolicyRepository;
import com.example.tradedemo.domain.coupon.repository.MemberCouponRepository;
import com.example.tradedemo.domain.members.entity.Member;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponPolicyRepository couponPolicyRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CouponHistoryRepository couponHistoryRepository;

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
        Duration policyDuration = CouponDuration.getPolicyDuration(request.getIssueType());
        Duration couponDuration = CouponDuration.getCouponDuration(request.getIssueType());

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
    public Page<SearchAllCouponPolicyResponse> searchAllCouponPolicies(
            String sortCreatedAt, String issueType, Pageable pageable) {
        return couponPolicyRepository.getAllCouponPolicy(sortCreatedAt, issueType, pageable);
    }

    @Transactional(readOnly = true)
    public Page<SearchAllMemberCouponResponse> getAllMemberCoupon(Long memberId, String status, Pageable pageable) {
        return memberCouponRepository.findAllMemberCouponByMemberId(memberId, status, pageable);
    }

    @Transactional(readOnly = true)
    public SearchAllMemberCouponResponse getMemberCoupon(Long memberId, Long couponId) {
        return memberCouponRepository
                .findMemberCouponByMemberIdAndMemberCouponId(memberId, couponId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_COUPON_NOT_FOUND));
    }

    @Transactional
    public void issueFirstComeCoupon(Long couponPolicyId, Member member) {
        // FIRST_COME 정책 조회 (존재하지 않거나 issueType이 다르면 예외)
        CouponPolicy couponPolicy = couponPolicyRepository
                .findByIdAndIssueType(couponPolicyId, IssueType.FIRST_COME)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_COUPON_POLICY_NOT_FOUND));

        // 발급 가능 여부 체크 (매진 여부)
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

        // 발급 수량 증가
        couponPolicy.increaseExpendQuantity();
    }
}
