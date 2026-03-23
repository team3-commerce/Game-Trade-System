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

    @Transactional(readOnly = true)
    public Item findItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new ServiceException(ErrorEnum.ERR_ITEM_NOT_FOUND)
        );
    }

    @Transactional(readOnly = true)
    public Page<Item> findItems(SearchItemRequest req) {
        return itemRepository.searchItem(req);
    }

    @Cacheable(
        value =  ITEM_CACHE_NAME, 
        key = "@itemCacheService.getItemIdCacheKey(#itemId)"
    )
    @Transactional(readOnly = true)
    public GetItemResponse getItemV2(Long itemId) {
        Item item = findItem(itemId);
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
}
