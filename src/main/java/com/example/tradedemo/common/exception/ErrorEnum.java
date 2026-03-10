package com.example.tradedemo.common.exception;

import com.example.tradedemo.common.consts.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorEnum {

    // Auth
    ERR_AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, ErrorMessage.MSG_AUTH_INVALID_TOKEN),
    ERR_AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, ErrorMessage.MSG_AUTH_EXPIRED_TOKEN),

    // Member
    ERR_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorMessage.MSG_MEMBER_NOT_FOUND);

    private final HttpStatus httpStatus;
    private final String errorMessage;
}
