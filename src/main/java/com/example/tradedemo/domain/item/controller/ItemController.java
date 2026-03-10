package com.example.tradedemo.domain.item.controller;

import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.item.dto.GetItemResponse;
import com.example.tradedemo.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor()
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/api/v1/items/{itemId}")
    public ResponseEntity<ApiResponse<GetItemResponse>> getProduct(@PathVariable Long itemId) {
        GetItemResponse res = itemService.getItem(itemId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }
}
