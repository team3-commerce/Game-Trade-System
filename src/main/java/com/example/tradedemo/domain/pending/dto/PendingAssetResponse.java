package com.example.tradedemo.domain.pending.dto;

import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PendingAssetResponse {

    private Long pendingAssetId;
    private Type type;
    private BigDecimal moneyAmount;
    private Long itemQuantity;

    /**
     * 수령 대기 자산 조회 응답
     * id, 돈/아이템, 돈이면 금액, 아이템이면 수량
     * @param asset
     * @return
     */
    public static PendingAssetResponse from(PendingAsset asset) {
        return new PendingAssetResponse(
                asset.getId(),
                asset.getType(),
                asset.getMoneyAmount(),
                asset.getItemQuantity()
        );
    }
}