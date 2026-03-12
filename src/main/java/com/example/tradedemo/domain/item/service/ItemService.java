package com.example.tradedemo.domain.item.service;

import com.example.tradedemo.domain.item.dto.GetItemResponse;
import com.example.tradedemo.domain.item.dto.SearchItemRequest;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.exception.ItemNotFoundException;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import com.example.tradedemo.common.dto.PageResponse;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public GetItemResponse getItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException());

        return GetItemResponse.of(item);
    }

    public PageResponse<GetItemResponse> getManyItems(SearchItemRequest req) {
        Page<Item> items = itemRepository.searchItem(req);
        return PageResponse.of(items.map(GetItemResponse::of));
    }
}
