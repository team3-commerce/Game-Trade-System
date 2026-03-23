package com.example.tradedemo.domain.item.facade;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.item.dto.GetItemResponse;
import com.example.tradedemo.domain.item.dto.SearchItemRequest;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.service.ItemCacheService;
import com.example.tradedemo.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ItemFacade {
    private final ItemService itemService;
    private final ItemCacheService itemCacheService;

    @Transactional(readOnly = true)
    public GetItemResponse getItem(Long itemId) {
        Item item = itemService.findItem(itemId);
        return GetItemResponse.of(item);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetItemResponse> getManyItems(SearchItemRequest req) {
        Page<Item> items = itemService.findItems(req);
        return PageResponse.of(items.map(GetItemResponse::of));
    }

    @Transactional(readOnly = true)
    public GetItemResponse getItemV2(Long itemId) {
        return itemService.getItemV2(itemId);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetItemResponse> getManyItemsV2(SearchItemRequest req) {
        return itemService.getManyItemsV2(req);
    }

    @Transactional(readOnly = true)
    public GetItemResponse getItemV3(Long itemId) {
        String cacheKey = itemCacheService.getItemIdCacheKey(itemId);

        GetItemResponse cached = itemCacheService.getItem(cacheKey);
        if (cached != null) {
            return cached;
        }

        Item item = itemService.findItem(itemId);
        GetItemResponse res = GetItemResponse.of(item);
        itemCacheService.setItem(cacheKey, res);

        return res;
    }

    @Transactional(readOnly = true)
    public PageResponse<GetItemResponse> getManyItemsV3(SearchItemRequest req) {
        boolean shouldCache = itemCacheService.shouldCacheSearchItemRequest(req);

        if (!shouldCache) {
            Page<Item> items = itemService.findItems(req);
            return PageResponse.of(items.map(GetItemResponse::of));
        }

        String cacheKey = itemCacheService.getSearchItemRequestCacheKey(req);

        PageResponse<GetItemResponse> cached = itemCacheService.getItemList(cacheKey);
        if (cached != null) {
            return cached;
        }

        Page<Item> items = itemService.findItems(req);
        PageResponse<GetItemResponse> res = PageResponse.of(items.map(GetItemResponse::of));

        itemCacheService.setItemList(cacheKey, res);

        return res;
    }
}
