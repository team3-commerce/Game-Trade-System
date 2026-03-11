package com.example.tradedemo.domain.marketlistings.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class MarketListingOverSellingException extends ServiceException {
    public MarketListingOverSellingException() {
        super(ErrorEnum.ERR_MARKET_LISTING_OVER_SELLING);
    }
}
