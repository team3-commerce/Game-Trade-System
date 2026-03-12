package com.example.tradedemo.domain.coupon.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.constants.CouponDuration;
import com.example.tradedemo.domain.coupon.dto.*;
import com.example.tradedemo.domain.coupon.entity.CouponHistory;
import com.example.tradedemo.domain.coupon.entity.CouponPolicy;
import com.example.tradedemo.domain.coupon.entity.MemberCoupon;
import com.example.tradedemo.domain.coupon.enums.IssueType;
import com.example.tradedemo.domain.coupon.repository.CouponHistoryRepository;
import com.example.tradedemo.domain.coupon.repository.CouponPolicyRepository;
import com.example.tradedemo.domain.coupon.repository.MemberCouponRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.entity.WalletHistories;
import com.example.tradedemo.domain.wallet.enums.WalletStatus;
import com.example.tradedemo.domain.wallet.repository.WalletHistoryRepository;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
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
    private final WalletRepository walletRepository;
    private final WalletHistoryRepository walletHistoryRepository;

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

    @Transactional
    public void useCoupon(Long memberId, Long memberCouponId, Member member) {
        // 본인 쿠폰인지 조회
        MemberCoupon memberCoupon = memberCouponRepository
                .findById(memberCouponId)
                .filter(mc -> mc.getMember().getId().equals(memberId))
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_COUPON_NOT_FOUND));

        // 사용 가능 여부 체크
        if (!memberCoupon.isUsable()) {
            throw new ServiceException(ErrorEnum.ERR_COUPON_NOT_USABLE);
        }

        // 지갑 조회
        Wallet wallet = walletRepository
                .findByMemberId(memberId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_WALLET_NOT_FOUND));

        memberCoupon.use();

        CouponHistory couponHistory = couponHistoryRepository.save(CouponHistory.create(member, memberCoupon));

        wallet.addBalance(memberCoupon.getCouponPolicy().getMoneyAmount());

        walletHistoryRepository.save(WalletHistories.create(
                memberCoupon.getCouponPolicy().getMoneyAmount(),
                WalletStatus.COUPON,
                wallet.getBalance(),
                wallet,
                couponHistory,
                member,
                null));
    }

    @Transactional(readOnly = true)
    public Page<SearchAllCouponHistoryResponse> getAllCouponHistory(
            Long memberId, String status, String sortCreatedAt, Pageable pageable) {
        return couponHistoryRepository.findAllCouponHistoryByMemberId(memberId, status, sortCreatedAt, pageable);
    }
}
