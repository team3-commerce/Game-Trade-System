package com.example.tradedemo.domain.coupon.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.coupon.constants.CouponMessage;
import com.example.tradedemo.domain.coupon.dto.*;
import com.example.tradedemo.domain.coupon.service.CouponService;
import com.example.tradedemo.domain.members.entity.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /**
     * 내 쿠폰 전체 조회
     * status 값을 전달하여 정렬 조건 추가 가능
     * status UNUSED/USED/EXPIRED 외 다른 값 입력 시 무시 -> 조건 추가 X
     */
    @GetMapping("/api/v1/me/coupons")
    public ResponseEntity<ApiResponse<PageResponse<SearchAllMemberCouponResponse>>> getAllMemberCoupon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Pageable pageable = PageRequest.of(page, 10);
        Long memberId = principalDetails.getMember().getId();
        PageResponse<SearchAllMemberCouponResponse> response =
                PageResponse.of(couponService.getAllMemberCoupon(memberId, status, pageable));

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), response));
    }

    /**
     * 내 쿠폰 단건 조회
     */
    @GetMapping("/api/v1/me/coupons/{couponId}")
    public ResponseEntity<ApiResponse<SearchAllMemberCouponResponse>> getMemberCoupon(
            @PathVariable Long couponId, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getMember().getId();

        SearchAllMemberCouponResponse response = couponService.getMemberCoupon(memberId, couponId);

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), response));
    }

    /**
     * 선착순 쿠폰 발급 신청
     */
    @PostMapping("/api/v1/coupon-policies/{couponPolicyId}/issue")
    public ResponseEntity<ApiResponse<String>> issueFirstComeCoupon(
            @PathVariable Long couponPolicyId, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Member member = principalDetails.getMember();
        couponService.issueFirstComeCoupon(couponPolicyId, member);

        return ResponseEntity.ok(
                ApiResponse.success(String.valueOf(HttpStatus.OK.value()), CouponMessage.COUPON_ISSUED));
    }

    /**
     * 내 쿠폰 사용
     */
    @PostMapping("/api/v1/me/coupons/{memberCouponId}/use")
    public ResponseEntity<ApiResponse<String>> useCoupon(
            @PathVariable Long memberCouponId, @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Long memberId = principalDetails.getMember().getId();
        Member member = principalDetails.getMember();
        couponService.useCoupon(memberId, memberCouponId, member);

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), CouponMessage.COUPON_USED));
    }

    /**
     * 내 쿠폰 사용 내역 조회
     * status 값을 USED/EXPIRED 로 전달하여 필터 조건 추가 가능
     * 그 외 값 입력 시 무시
     */
    @GetMapping("/api/v1/me/coupons-histories")
    public ResponseEntity<ApiResponse<PageResponse<SearchAllCouponHistoryResponse>>> getAllCouponHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortCreatedAt,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Pageable pageable = PageRequest.of(page, 10);
        Long memberId = principalDetails.getMember().getId();
        PageResponse<SearchAllCouponHistoryResponse> response =
                PageResponse.of(couponService.getAllCouponHistory(memberId, status, sortCreatedAt, pageable));

        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), response));
    }
}
