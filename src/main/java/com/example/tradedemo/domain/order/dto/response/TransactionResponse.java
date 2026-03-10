package com.example.tradedemo.domain.order.dto.response;

import com.example.tradedemo.domain.order.entity.Order;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class TransactionResponse {

    private final Long orderId;
    private final BigDecimal transactionMoney;
    private final Long transactionStock;
    private final Long marketListingId;
    private final LocalDateTime createdAt;

    private TransactionResponse(
            Long orderId,
            BigDecimal transactionMoney,
            Long transactionStock,
            Long marketListingId,
            LocalDateTime createdAt
    ){
        this.orderId = orderId;
        this.transactionMoney = transactionMoney;
        this.transactionStock = transactionStock;
        this.marketListingId = marketListingId;
        this.createdAt = createdAt;
    }

    public static TransactionResponse of(Order order){
        return new TransactionResponse(
                order.getId(),
                order.getTransactionMoney(),
                order.getTransactionStock(),
                order.getMarketListing().getId(),
                order.getCreatedAt()
        );
    }
}