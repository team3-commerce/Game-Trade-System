package com.example.tradedemo.domain.coupon.controller;

import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.coupon.dto.CreateCouponPolicyRequest;
import com.example.tradedemo.domain.coupon.dto.CreateCouponPolicyResponse;
import com.example.tradedemo.domain.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/api/v1/admin/coupon-policies")
    public ResponseEntity<ApiResponse<CreateCouponPolicyResponse>> createCouponPolicy(
            @Valid @RequestBody CreateCouponPolicyRequest request) {
        CreateCouponPolicyResponse response = couponService.createCouponPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), response));
    }
}
