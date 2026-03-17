package com.example.tradedemo.domain.item.service;

import com.example.tradedemo.domain.item.dto.GetItemResponse;
import com.example.tradedemo.domain.item.dto.SearchItemRequest;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.common.exception.ErrorEnum;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public GetItemResponse getItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_ITEM_NOT_FOUND)
        );

        return GetItemResponse.of(item);
    }

    @Transactional(readOnly = true)
    public PageResponse<GetItemResponse> getManyItems(SearchItemRequest req) {
        Page<Item> items = itemRepository.searchItem(req);
        return PageResponse.of(items.map(GetItemResponse::of));
    }

    @Cacheable(value =  "items", key = "#itemId")
    @Transactional(readOnly = true)
    public GetItemResponse getItemV2(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_ITEM_NOT_FOUND)
        );

        return GetItemResponse.of(item);
    }

    @Cacheable(
        value =  "itemsSearches",
        key = "@itemService.getSearchItemRequestCacheKey(#req)",
        condition = "@itemService.shouldCacheSearchItemRequest(#req)"
    )
    @Transactional(readOnly = true)
    public PageResponse<GetItemResponse> getManyItemsV2(SearchItemRequest req) {
        Page<Item> items = itemRepository.searchItem(req);
        return PageResponse.of(items.map(GetItemResponse::of));
    }

    public boolean shouldCacheSearchItemRequest(SearchItemRequest req) {
        if (req.getNormalizedKeyword() == null && req.getPage() < 5) {
            return true;
        }
        return false;
    }

    public String getSearchItemRequestCacheKey(SearchItemRequest req) {
        String itemTypeKey;
        if (req.getItemType() == null) {
            itemTypeKey = "";
        }else {
            itemTypeKey = req.getItemType().toString();
        }

        String createdAtSortKey;
        if (req.shouldSortCreatedAtAsc()) {
            createdAtSortKey = "sort-created-at-asc";
        }else {
            createdAtSortKey = "sort-created-at-dsc";
        }

        return req.getPage().toString() + ":" + itemTypeKey + ":" + createdAtSortKey;
    }
}
