package com.example.tradedemo.domain.item.service;

import static com.example.tradedemo.domain.item.consts.ItemConst.*;

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
    private final ItemCacheService itemCacheService;

    @Transactional(readOnly = true)
    public Item findItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_ITEM_NOT_FOUND)
        );
    }

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

    @Cacheable(
        value =  ITEM_CACHE_NAME, 
        key = "@itemCacheService.getItemIdCacheKey(#itemId)"
    )
    @Transactional(readOnly = true)
    public GetItemResponse getItemV2(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_ITEM_NOT_FOUND)
        );

        return GetItemResponse.of(item);
    }

    @Cacheable(
        value =  ITEM_SEARCH_CACHE_NAME,
        key = "@itemCacheService.getSearchItemRequestCacheKey(#req)",
        condition = "@itemCacheService.shouldCacheSearchItemRequest(#req)"
    )
    @Transactional(readOnly = true)
    public PageResponse<GetItemResponse> getManyItemsV2(SearchItemRequest req) {
        Page<Item> items = itemRepository.searchItem(req);
        return PageResponse.of(items.map(GetItemResponse::of));
    }

    @Transactional(readOnly = true)
    public GetItemResponse getItemV3(Long itemId) {
        String cacheKey = itemCacheService.getItemIdCacheKey(itemId);

        GetItemResponse cached = itemCacheService.getItem(cacheKey);
        if (cached != null) {
            return cached;
        }

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_ITEM_NOT_FOUND)
        );

        GetItemResponse res = GetItemResponse.of(item);
        itemCacheService.setItem(cacheKey, res);

        return res;
    }

    @Transactional(readOnly = true)
    public PageResponse<GetItemResponse> getManyItemsV3(SearchItemRequest req) {
        boolean shouldCache = itemCacheService.shouldCacheSearchItemRequest(req);

        // 만약 cache를 안하기로 한 요청이라면 redis를 부르지도 않았을 것이기 때문에 DB로 바로 조회
        if (!shouldCache) {
            Page<Item> items = itemRepository.searchItem(req);
            return PageResponse.of(items.map(GetItemResponse::of));
        }

        String cacheKey = itemCacheService.getSearchItemRequestCacheKey(req);

        PageResponse<GetItemResponse> cached = itemCacheService.getItemList(cacheKey);
        if (cached != null) {
            return cached;
        }

        Page<Item> items = itemRepository.searchItem(req);
        PageResponse<GetItemResponse> res = PageResponse.of(items.map(GetItemResponse::of));

        itemCacheService.setItemList(cacheKey, res);

        return res;
    }
}
