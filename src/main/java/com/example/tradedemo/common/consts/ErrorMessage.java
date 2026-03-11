package com.example.tradedemo.common.consts;

public final class ErrorMessage {

    // Auth
    public static final String MSG_AUTH_INVALID_TOKEN = "유효하지 않은 토큰입니다.";
    public static final String MSG_AUTH_EXPIRED_TOKEN = "만료된 토큰입니다.";
    public static final String MSG_AUTH_DUPLICATE_EMAIL = "이미 존재하는 이메일입니다.";
    public static final String MSG_AUTH_MEMBER_NOT_FOUND = "존재하지 않는 이메일입니다.";
    public static final String MSG_AUTH_INVALID_PASSWORD = "올바르지 않은 비밀번호입니다.";

    // Member
    public static final String MSG_MEMBER_NOT_FOUND = "찾는 유저 정보가 존재하지 않습니다";

    // Item
    public static final String MSG_ITEM_NOT_FOUND = "찾는 아이템 정보가 존재하지 않습니다";

    // MarketListing
    public static final String MSG_MEMBERITEM_NOT_FOUND = "인벤토리 내 아이템 정보가 존재하지 않습니다";

    // Order

    // Wallet

    // Coupon
    public static final String MSG_COUPON_POLICY_DUPLICATE_NAME = "이미 존재하는 쿠폰 정책 이름입니다";
    public static final String MSG_COUPON_POLICY_FIRST_COME_QUANTITY_REQUIRED = "선착순 쿠폰은 총 수량이 필수입니다";
}
