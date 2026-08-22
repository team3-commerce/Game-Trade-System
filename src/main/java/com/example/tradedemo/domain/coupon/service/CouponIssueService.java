package com.example.tradedemo.domain.coupon.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.entity.CouponPolicy;
import com.example.tradedemo.domain.coupon.entity.MemberCoupon;
import com.example.tradedemo.domain.coupon.enums.IssueType;
import com.example.tradedemo.domain.coupon.repository.CouponPolicyRepository;
import com.example.tradedemo.domain.coupon.repository.MemberCouponRepository;
import com.example.tradedemo.domain.members.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponIssueService {
    private final CouponPolicyRepository couponPolicyRepository;
    private final MemberCouponRepository memberCouponRepository;

    /**
     * DeadLock 발생 -> 기존 로직을 트랜잭션 분리해 해결
     */
    @Transactional
    public void issueFirstComeCouponV2(Long couponPolicyId, Member member) {
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
}
