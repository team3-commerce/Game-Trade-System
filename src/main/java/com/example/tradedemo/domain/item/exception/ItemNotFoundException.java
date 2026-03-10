package com.example.tradedemo.domain.item.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class ItemNotFoundException extends ServiceException {
    public ItemNotFoundException() {
        super(ErrorEnum.ERR_ITEM_NOT_FOUND);
    }
}
