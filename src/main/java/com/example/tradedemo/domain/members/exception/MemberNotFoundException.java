package com.example.tradedemo.domain.members.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class MemberNotFoundException extends ServiceException {
    public MemberNotFoundException() {
        super(ErrorEnum.ERR_MEMBER_NOT_FOUND);
    }
}
