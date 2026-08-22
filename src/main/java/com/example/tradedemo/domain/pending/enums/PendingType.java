package com.example.tradedemo.domain.pending.enums;

public enum PendingType {
    SALE_SUCCESS, // 판매 중
    PURCHASE_SUCCESS, // 구매됨(판매됨)
    EXPIRED, // 만료
    CANCELLED // 판매 취소
}
