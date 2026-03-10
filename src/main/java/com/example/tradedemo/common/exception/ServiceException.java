package com.example.tradedemo.common.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final ErrorEnum errorEnum;

    public ServiceException(ErrorEnum errorEnum) {
        super(errorEnum.getErrorMessage());
        this.errorEnum = errorEnum;
    }
}
