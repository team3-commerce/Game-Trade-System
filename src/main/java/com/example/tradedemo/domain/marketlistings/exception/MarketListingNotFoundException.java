package com.example.tradedemo.domain.marketlistings.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class MarketListingNotFoundException extends ServiceException {
    public MarketListingNotFoundException() {
        super(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND);
    }
}
