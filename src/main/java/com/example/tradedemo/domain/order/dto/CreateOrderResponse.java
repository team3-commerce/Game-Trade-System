package com.example.tradedemo.domain.order.dto;

import com.example.tradedemo.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CreateOrderResponse {
    private final Long orderId;
    private final BigDecimal transactionMoney;
    private final Long transactionStock;
    private final String itemName;

    public static CreateOrderResponse create(Order order, String itemName) {
        return new CreateOrderResponse(
              order.getId(),
              order.getTransactionMoney(),
              order.getTransactionStock(),
              itemName
        );
    }
}
