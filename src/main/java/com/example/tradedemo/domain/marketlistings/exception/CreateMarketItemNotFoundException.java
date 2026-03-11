package com.example.tradedemo.domain.marketlistings.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class CreateMarketItemNotFoundException extends ServiceException {
    public CreateMarketItemNotFoundException() {
        super(ErrorEnum.ERR_MEMBERITEM_NOT_FOUND);
    }
}
