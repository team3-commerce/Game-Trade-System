package com.example.tradedemo.domain.members.exception;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;

public class InventoryItemNotFoundException extends ServiceException {
    public InventoryItemNotFoundException() {
        super(ErrorEnum.ERR_INVENTORYITEM_NOT_FOUND);
    }
}
