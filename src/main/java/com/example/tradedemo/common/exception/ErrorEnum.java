package com.example.tradedemo.common.exception;

import com.example.tradedemo.common.consts.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorEnum {
    // Item
    ERR_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorMessage.MSG_ITEM_NOT_FOUND),

    // Member
    ERR_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorMessage.MSG_MEMBER_NOT_FOUND);

    private final HttpStatus httpStatus;
    private final String errorMessage;
}
