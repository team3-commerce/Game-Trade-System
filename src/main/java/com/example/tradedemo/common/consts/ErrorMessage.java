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
    public static final String MSG_AUTH_SOCIAL_ACCOUNT_ONLY = "소셜 로그인을 이용하거나 비밀번호를 설정해주세요";
    public static final String MSG_AUTH_SOCIAL_UNLINK_FORBIDDEN = "최소 하나의 로그인 수단이 필요합니다";
    public static final String MSG_AUTH_SOCIAL_NOT_FOUND = "연동된 소셜 계정을 찾을 수 없습니다";
    public static final String MSG_AUTH_SOCIAL_UNSUPPORTED_PROVIDER = "지원하지 않는 소셜 로그인 제공자입니다";
    public static final String MSG_AUTH_NOT_ACTIVE_STATUS = "활성화된 계정이 아닙니다";
    public static final String MSG_AUTH_PASSWORD_ALREADY_EXISTS = "이미 비밀번호가 설정되어 있습니다";

    // Member
    public static final String MSG_MEMBER_NOT_FOUND = "찾는 유저 정보가 존재하지 않습니다";
    public static final String MSG_MEMBER_ITEM_NOT_FOUND = "찾는 유저 아이템 정보가 존재하지 않습니다";
    public static final String MSG_MEMBER_HAS_ACTIVE_LISTINGS = "판매 중인 상품이 있습니다";
    public static final String MSG_MEMBER_HAS_PENDING_ASSETS = "수령 대기 중인 자산이나 아이템이 있습니다";

    // Item
    public static final String MSG_ITEM_NOT_FOUND = "찾는 아이템 정보가 존재하지 않습니다";
    public static final String MSG_INVENTORYITEM_NOT_FOUND = "아이템은 가지고 있는 것보다 많이 등록될 수 없습니다";

    // MarketListing
    public static final String MSG_MARKET_LISTING_NOT_FOUND = "찾는 거래 매물 정보가 존재하지 않습니다";
    public static final String MSG_MARKET_LISTING_OWNER_MISMATCH = "아이템 소유자와 등록자가 다릅니다";
    public static final String MSG_MARKET_LISTING_OVER_SELLING = "인벤토리 내 아이템 정보가 존재하지 않습니다";
    public static final String MSG_MARKET_LISTING_FORBIDDEN_FROM_CANCEL = "거래 매물을 삭제할 권한이 없습니다";
    public static final String MSG_MARKET_LISTING_ILLEGAL_CANCEL_STATUS = "거래 매물이 취소 불가능한 상태 입니다";

    // Order
    public static final String MSG_WALLET_INSUFFICIENT_BALANCE_BAD_REQUEST = "잔액이 부족합니다";
    public static final String MSG_MARKET_LISTING_NOT_SELLING = "구매 가능한 상품이 아닙니다";

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
    public static final String MSG_COUPON_EXPIRED = "만료된 쿠폰입니다";
    public static final String MSG_COUPON_LOCK_CONFLICT = "현재 요청이 많아 잠시 후 다시 시도해주세요";

    // pending
    public static final String MSG_PENDING_ASSET_FOUND_EXCEPTION = "수령 대기 자산이 없습니다.";
    public static final String MSG_PENDING_ASSET_ALREADY_CLAIMED = "이미 수령한 자산입니다";
    public static final String MSG_PENDING_ASSET_FORBIDDEN = "본인의 수령 대기 자산만 수령할 수 있습니다";
    public static final String MSG_PENDING_ASSET_EXPIRED_EXCEPTION = "수령 대기 기간 만료 상태입니다";



}
