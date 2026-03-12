package com.example.tradedemo.domain.coupon.repository;

import com.example.tradedemo.domain.coupon.dto.SearchAllMemberCouponResponse;
import com.example.tradedemo.domain.coupon.entity.MemberCoupon;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberCouponCustomRepository {

    Page<SearchAllMemberCouponResponse> findAllMemberCouponByMemberId(Long memberId, String status, Pageable pageable);

    Optional<SearchAllMemberCouponResponse> findMemberCouponByMemberIdAndMemberCouponId(Long memberId, Long couponId);

    List<MemberCoupon> findAllExpiredCoupons(LocalDateTime now);
}
