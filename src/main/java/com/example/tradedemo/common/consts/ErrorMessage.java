package com.example.tradedemo.common.consts;

public final class ErrorMessage {

    // Auth
    public static final String MSG_AUTH_INVALID_TOKEN = "유효하지 않은 토큰입니다";
    public static final String MSG_AUTH_EXPIRED_TOKEN = "만료된 토큰입니다";
    public static final String MSG_AUTH_DUPLICATE_EMAIL = "이미 존재하는 이메일입니다";
    public static final String MSG_AUTH_DUPLICATE_NICKNAME = "이미 존재하는 닉네임입니다";
    public static final String MSG_AUTH_MEMBER_NOT_FOUND = "존재하지 않는 이메일입니다";
    public static final String MSG_AUTH_INVALID_PASSWORD = "올바르지 않은 비밀번호입니다";
    public static final String MSG_AUTH_WITHDRAWN_MEMBER = "탈퇴 처리 된 회원입니다";
    public static final String MSG_AUTH_SUSPENDED_MEMBER = "관리자에 의해 정지된 회원입니다 (사유: %s)";

    // Member
    public static final String MSG_MEMBER_NOT_FOUND = "찾는 유저 정보가 존재하지 않습니다";
    public static final String MSG_MEMBER_ITEM_NOT_FOUND = "찾는 유저 아이템 정보가 존재하지 않습니다";

    // Item
    public static final String MSG_ITEM_NOT_FOUND = "찾는 아이템 정보가 존재하지 않습니다";

    // MarketListing
    public static final String MSG_MARKET_LISTING_NOT_FOUND = "찾는 거래 매물 정보가 존재하지 않습니다";

    // Order

    // Wallet
    public static final String MSG_WALLET_NOT_FOUND = "지갑을 찾을 수 없습니다";

    // Coupon
    public static final String MSG_COUPON_POLICY_DUPLICATE_NAME = "이미 존재하는 쿠폰 정책 이름입니다";
    public static final String MSG_COUPON_POLICY_FIRST_COME_QUANTITY_REQUIRED = "선착순 쿠폰은 총 수량이 필수입니다";
    public static final String MSG_COUPON_POLICY_AUTO_SIGNUP_ALREADY_EXISTS = "회원가입 자동 발급 쿠폰 정책은 하나만 존재할 수 있습니다";
    public static final String MSG_COUPON_ALREADY_ISSUED = "이미 발급된 쿠폰입니다";
    public static final String MSG_MEMBER_COUPON_NOT_FOUND = "찾는 쿠폰의 정보가 존재하지 않습니다";
    public static final String MSG_COUPON_POLICY_NOT_FOUND = "쿠폰 정책을 찾을 수 없습니다";
    public static final String MSG_COUPON_POLICY_SOLD_OUT = "선착순 쿠폰이 마감되었습니다";
    public static final String MSG_COUPON_NOT_USABLE = "사용할 수 없는 쿠폰입니다";
}
