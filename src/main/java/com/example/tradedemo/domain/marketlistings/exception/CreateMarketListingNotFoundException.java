package com.example.tradedemo.domain.marketlistings.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class CreateMarketListingNotFoundException extends ServiceException {
    public CreateMarketListingNotFoundException() {
        super(ErrorEnum.ERR_MEMBERITEM_NOT_FOUND);
    }
}
