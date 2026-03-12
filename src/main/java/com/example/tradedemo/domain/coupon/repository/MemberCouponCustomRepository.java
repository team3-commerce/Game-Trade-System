package com.example.tradedemo.domain.coupon.repository;

import com.example.tradedemo.domain.coupon.dto.SearchAllMemberCouponResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberCouponCustomRepository {

    Page<SearchAllMemberCouponResponse> findAllMemberCouponByMemberId(Long memberId, String status, Pageable pageable);

    Optional<SearchAllMemberCouponResponse> findMemberCouponByMemberIdAndMemberCouponId(Long memberId, Long couponId);
}
