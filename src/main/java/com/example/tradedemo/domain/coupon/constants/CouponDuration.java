package com.example.tradedemo.domain.coupon.constants;

import com.example.tradedemo.domain.coupon.enums.IssueType;
import java.time.Duration;

public final class CouponDuration {

    // 정책 유효기간
    public static final Duration FIRST_COME_POLICY = Duration.ofDays(3); // 선착순 이벤트 3일

    // 쿠폰 유효기간
    public static final Duration FIRST_COME_COUPON = Duration.ofDays(7); // 선착순 쿠폰 7일
    public static final Duration AUTO_SIGNUP_COUPON = null; // 회원가입 쿠폰 무제한

    private CouponDuration() {}

    public static Duration getPolicyDuration(IssueType issueType) {
        return switch (issueType) {
            case FIRST_COME -> FIRST_COME_POLICY;
            case AUTO_SIGNUP -> null; // 정책 만료 없음
        };
    }

    public static Duration getCouponDuration(IssueType issueType) {
        return switch (issueType) {
            case FIRST_COME -> FIRST_COME_COUPON;
            case AUTO_SIGNUP -> AUTO_SIGNUP_COUPON; // null (무제한)
        };
    }
}
