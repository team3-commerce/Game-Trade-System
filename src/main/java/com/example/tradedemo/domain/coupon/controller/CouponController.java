package com.example.tradedemo.domain.coupon.controller;

import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.coupon.dto.CreateCouponPolicyRequest;
import com.example.tradedemo.domain.coupon.dto.CreateCouponPolicyResponse;
import com.example.tradedemo.domain.coupon.dto.PageResponse;
import com.example.tradedemo.domain.coupon.dto.SearchAllCouponPolicyResponse;
import com.example.tradedemo.domain.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 쿠폰 정책 조회
     * sortCreatedAt 값을 asc/desc 로 전달하여 정렬 조건 추가 가능
     * issueType 값을 FIRST_COME/AUTO_SIGNUP 으로 전달하여 정렬 조건 추가 가능
     * issueType FIRST_COME/AUTO_SIGNUP 외 다른 값 입력 시 무시 -> 조건 추가 X
     */
    @GetMapping("/api/v1/coupon-policies")
    public ResponseEntity<ApiResponse<PageResponse<SearchAllCouponPolicyResponse>>> searchAllCouponPolicies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String sortCreatedAt,
            @RequestParam(required = false) String issueType) {

        Pageable pageable = PageRequest.of(page, 10);

        PageResponse<SearchAllCouponPolicyResponse> response =
                PageResponse.of(couponService.searchAllCouponPolicies(sortCreatedAt, issueType, pageable));

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), response));
    }
}
