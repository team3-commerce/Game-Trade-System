package com.example.tradedemo.domain.coupon.constants;

public final class CouponCacheConst {

    // 쿠폰 정책 목록
    public static final long COUPON_POLICIES_TTL_MINUTES  = 10;

    // 내 쿠폰 목록/단건
    public static final long MEMBER_COUPONS_TTL_MINUTES   = 5;

    // 쿠폰 사용 내역: 사용 시
    public static final long COUPON_HISTORIES_TTL_MINUTES = 10;


    // 쿠폰 정책 목록 캐시 키
    public static final String POLICIES_PREFIX  = "coupon:policies:";

    // 내 쿠폰(목록/단건) 캐시 키
    public static final String COUPONS_PREFIX   = "coupon:member:";

    // 쿠폰 사용 내역 캐시 키
    public static final String HISTORIES_PREFIX = "coupon:histories:member:";

    // 쿠폰 사용 내역 캐시 키
    public static final String RAN_OUT_PREFIX = "coupon:ran_out:";

    private CouponCacheConst() {}
}
