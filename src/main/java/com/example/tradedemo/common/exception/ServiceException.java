package com.example.tradedemo.common.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final ErrorEnum errorEnum;
    private final String customMessage;

    public ServiceException(ErrorEnum errorEnum) {
        super(errorEnum.getErrorMessage());
        this.errorEnum = errorEnum;
        this.customMessage = errorEnum.getErrorMessage();
    }

    public ServiceException(ErrorEnum errorEnum, Object... args) {
        super(String.format(errorEnum.getErrorMessage(), args));
        this.errorEnum = errorEnum;
        this.customMessage = String.format(errorEnum.getErrorMessage(), args);
    }
}
