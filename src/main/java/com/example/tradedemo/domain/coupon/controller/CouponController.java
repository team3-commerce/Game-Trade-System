package com.example.tradedemo.domain.coupon.controller;

import com.example.tradedemo.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
}
