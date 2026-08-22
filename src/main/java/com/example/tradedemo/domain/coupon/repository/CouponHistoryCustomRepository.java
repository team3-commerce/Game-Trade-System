package com.example.tradedemo.domain.coupon.repository;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.coupon.dto.SearchAllCouponHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponHistoryCustomRepository {
    PageResponse<SearchAllCouponHistoryResponse> findAllCouponHistoryByMemberId(
            Long memberId, String status, String sortCreatedAt, Pageable pageable);
}
