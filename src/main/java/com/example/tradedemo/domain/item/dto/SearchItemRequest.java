package com.example.tradedemo.domain.item.dto;

import com.example.tradedemo.domain.item.enums.ItemType;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchItemRequest {
    @Min(0)
    Integer page = 0;

    String keyword;
    ItemType itemType;
}
