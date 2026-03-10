package com.example.tradedemo.common.exception;

import com.example.tradedemo.common.consts.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorEnum {

    // Member
    ERR_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorMessage.MSG_MEMBER_NOT_FOUND),

    // Coupon
    ERR_COUPON_POLICY_DUPLICATE_NAME(HttpStatus.CONFLICT, ErrorMessage.MSG_COUPON_POLICY_DUPLICATE_NAME),
    ERR_COUPON_POLICY_FIRST_COME_QUANTITY_REQUIRED(
            HttpStatus.BAD_REQUEST, ErrorMessage.MSG_COUPON_POLICY_FIRST_COME_QUANTITY_REQUIRED);

    private final HttpStatus httpStatus;
    private final String errorMessage;
}
