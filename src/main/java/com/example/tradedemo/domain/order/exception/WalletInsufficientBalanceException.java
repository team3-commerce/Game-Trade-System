package com.example.tradedemo.domain.order.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class WalletInsufficientBalanceException extends ServiceException {
    public WalletInsufficientBalanceException() {
        super(ErrorEnum.ERR_WALLET_INSUFFICIENT_BALANCE_BAD_REQUEST);
    }
}
