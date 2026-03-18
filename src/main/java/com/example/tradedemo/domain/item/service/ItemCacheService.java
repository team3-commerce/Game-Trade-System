package com.example.tradedemo.domain.item.service;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.item.consts.ItemConst;
import com.example.tradedemo.domain.item.dto.GetItemResponse;
import com.example.tradedemo.domain.item.dto.SearchItemRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void setItem(String key, GetItemResponse result) {
        redisTemplate.opsForValue().set(key, result, ItemConst.ITEM_CACHE_TTL);
    }

    public void setItemList(String key, PageResponse<GetItemResponse> result) {
        redisTemplate.opsForValue().set(key, result, ItemConst.ITEM_CACHE_LIST_TTL);
    }

    public Optional<GetItemResponse> getItem(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(objectMapper.convertValue(value, new TypeReference<GetItemResponse>() {}));
    }

    public Optional<PageResponse<GetItemResponse>> getItemList(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(objectMapper.convertValue(value, new TypeReference<PageResponse<GetItemResponse>>() {}));
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
        } else {
            itemTypeKey = req.getItemType().toString();
        }

        String createdAtSortKey;
        if (req.shouldSortCreatedAtAsc()) {
            createdAtSortKey = "sort-created-at-asc";
        } else {
            createdAtSortKey = "sort-created-at-dsc";
        }

        return ItemConst.ITEM_CACHE_LIST_PREFIX + req.getPage().toString() + ":" + itemTypeKey + ":" + createdAtSortKey;
    }

    public String getItemIdCacheKey(Long itemId) {
        return ItemConst.ITEM_CACHE_PREFIX + itemId.toString();
    }
}
