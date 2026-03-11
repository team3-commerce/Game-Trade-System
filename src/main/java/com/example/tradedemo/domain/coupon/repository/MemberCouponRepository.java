package com.example.tradedemo.domain.coupon.repository;

import com.example.tradedemo.domain.coupon.entity.CouponPolicy;
import com.example.tradedemo.domain.coupon.entity.MemberCoupon;
import com.example.tradedemo.domain.members.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long>, MemberCouponCustomRepository {
    // 중복 발급 방지 체크
    boolean existsByMemberAndCouponPolicy(Member member, CouponPolicy couponPolicy);
}