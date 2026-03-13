package com.example.tradedemo.domain.wallet.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class WalletNotFoundException extends ServiceException {
    public WalletNotFoundException() {
        super(ErrorEnum.ERR_WALLET_NOT_FOUND);
    }
}
