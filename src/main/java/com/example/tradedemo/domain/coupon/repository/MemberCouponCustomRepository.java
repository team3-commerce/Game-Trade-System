package com.example.tradedemo.domain.coupon.repository;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.coupon.dto.SearchAllMemberCouponResponse;
import com.example.tradedemo.domain.coupon.entity.MemberCoupon;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface MemberCouponCustomRepository {

    PageResponse<SearchAllMemberCouponResponse> findAllMemberCouponByMemberId(Long memberId, String status, Pageable pageable);

    Optional<SearchAllMemberCouponResponse> findMemberCouponByMemberIdAndMemberCouponId(Long memberId, Long couponId);

    Optional<MemberCoupon> findMemberCouponForUse(Long memberId, Long memberCouponId);

    List<MemberCoupon> findAllExpiredCoupons(LocalDateTime now);
}
