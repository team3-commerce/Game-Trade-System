package com.example.tradedemo.domain.coupon.repository;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.coupon.dto.SearchAllCouponPolicyResponse;
import org.springframework.data.domain.Pageable;

public interface CouponPolicyCustomRepository {
    PageResponse<SearchAllCouponPolicyResponse> getAllCouponPolicy(String sortCreatedAt, String issueType, Pageable pageable);
}
