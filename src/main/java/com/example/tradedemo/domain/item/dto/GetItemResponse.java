package com.example.tradedemo.domain.item.dto;

import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.enums.ItemType;

public record GetItemResponse(Long itemId, String itemName, ItemType itemType) {
    public static GetItemResponse of(Item item) {
        return new GetItemResponse(item.getId(), item.getName(), item.getItemType());
    }
}
