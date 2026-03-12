package com.example.tradedemo.domain.coupon.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class CouponExpiredException extends ServiceException {
    public CouponExpiredException() {
        super(ErrorEnum.ERR_COUPON_EXPIRED);
    }
}
