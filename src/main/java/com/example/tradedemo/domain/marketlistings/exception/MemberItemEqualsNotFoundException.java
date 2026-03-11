package com.example.tradedemo.domain.marketlistings.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class MemberItemEqualsNotFoundException extends ServiceException {
    public MemberItemEqualsNotFoundException() {
        super(ErrorEnum.ERR_MEMBERITEM_EQUAL_NOT_FOUND);
    }
}
