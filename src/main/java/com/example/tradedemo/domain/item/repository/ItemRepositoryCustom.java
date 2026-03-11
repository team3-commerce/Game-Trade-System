package com.example.tradedemo.domain.item.repository;

import com.example.tradedemo.domain.item.dto.SearchItemRequest;
import com.example.tradedemo.domain.item.entity.Item;
import org.springframework.data.domain.Page;

public interface ItemRepositoryCustom {
    Page<Item> searchItem(SearchItemRequest searchTerm);
}
