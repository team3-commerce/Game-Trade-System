package com.example.tradedemo.domain.coupon.enums;

public enum CouponStatus {
    UNUSED(0),
    USED(1),
    EXPIRED(2);

    private final int order;

    CouponStatus(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
