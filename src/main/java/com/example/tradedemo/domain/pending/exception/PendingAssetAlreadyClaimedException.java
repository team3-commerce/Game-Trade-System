package com.example.tradedemo.domain.pending.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class PendingAssetAlreadyClaimedException extends ServiceException {
    public PendingAssetAlreadyClaimedException() {
        super(ErrorEnum.ERR_PENDING_ASSET_ALREADY_CLAIMED);
    }
}
