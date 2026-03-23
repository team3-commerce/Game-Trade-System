package com.example.tradedemo.domain.item.controller;

import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.item.dto.GetItemResponse;
import com.example.tradedemo.domain.item.dto.SearchItemRequest;
import com.example.tradedemo.domain.item.facade.ItemFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor()
public class ItemController {

    private final ItemFacade itemFacade;

    @GetMapping("/api/v1/items/{itemId}")
    public ResponseEntity<ApiResponse<GetItemResponse>> getItem(@PathVariable Long itemId) {
        GetItemResponse res = itemFacade.getItem(itemId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }

    @GetMapping("/api/v1/items")
    public ResponseEntity<ApiResponse<PageResponse<GetItemResponse>>> getManyItem(
            @Valid @ModelAttribute SearchItemRequest req) {
        PageResponse<GetItemResponse> res = itemFacade.getManyItems(req);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }

    @GetMapping("/api/v2/items/{itemId}")
    public ResponseEntity<ApiResponse<GetItemResponse>> getItemV2(@PathVariable Long itemId) {
        GetItemResponse res = itemFacade.getItemV2(itemId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }

    @GetMapping("/api/v2/items")
    public ResponseEntity<ApiResponse<PageResponse<GetItemResponse>>> getManyItemV2(
            @Valid @ModelAttribute SearchItemRequest req) {
        PageResponse<GetItemResponse> res = itemFacade.getManyItemsV2(req);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }

    @GetMapping("/api/v3/items/{itemId}")
    public ResponseEntity<ApiResponse<GetItemResponse>> getItemV3(@PathVariable Long itemId) {
        GetItemResponse res = itemFacade.getItemV3(itemId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }

    @GetMapping("/api/v3/items")
    public ResponseEntity<ApiResponse<PageResponse<GetItemResponse>>> getManyItemV3(
            @Valid @ModelAttribute SearchItemRequest req) {
        PageResponse<GetItemResponse> res = itemFacade.getManyItemsV3(req);

        return ResponseEntity.status(HttpStatus.OK)
                       .body(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }
}
