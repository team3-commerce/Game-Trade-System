package com.example.tradedemo.domain.pending.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class PendingAssetNotFoundException extends ServiceException {
    public PendingAssetNotFoundException() {
        super(ErrorEnum.ERR_PENDING_ASSET_FOUND_EXCEPTION);
    }
}
