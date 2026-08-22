package com.example.tradedemo.domain.members.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class MemberItemNotFoundException extends ServiceException {
    public MemberItemNotFoundException() {
        super(ErrorEnum.ERR_MEMBER_ITEM_NOT_FOUND);
    }
}
