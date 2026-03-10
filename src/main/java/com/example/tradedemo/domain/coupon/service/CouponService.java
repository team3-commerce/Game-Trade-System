package com.example.tradedemo.domain.coupon.service;

import com.example.tradedemo.domain.coupon.repository.CouponHistoryRepository;
import com.example.tradedemo.domain.coupon.repository.CouponPolicyRepository;
import com.example.tradedemo.domain.coupon.repository.MemberCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {
    private CouponPolicyRepository couponPolicyRepository;
    private MemberCouponRepository memberCouponRepository;
    private CouponHistoryRepository couponHistoryRepository;
}
