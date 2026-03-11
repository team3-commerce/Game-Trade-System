package com.example.tradedemo.domain.marketlistings.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class MarketListingOwnerMismatchException extends ServiceException {
    public MarketListingOwnerMismatchException() {
        super(ErrorEnum.ERR_MARKET_LISTING_OWNER_MISMATCH);
    }
}
