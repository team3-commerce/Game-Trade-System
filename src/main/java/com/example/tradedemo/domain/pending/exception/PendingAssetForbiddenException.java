package com.example.tradedemo.domain.pending.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class PendingAssetForbiddenException extends ServiceException {
    public PendingAssetForbiddenException() {
        super(ErrorEnum.ERR_PENDING_ASSET_FORBIDDEN);
    }
}
