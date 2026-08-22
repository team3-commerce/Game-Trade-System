package com.example.tradedemo.domain.marketlistings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchTrendingKeywordResponse {
    private final String keyword;
    private final Long searchCount;

    public static SearchTrendingKeywordResponse create(String keyword, Long searchCount) {
        return new SearchTrendingKeywordResponse(keyword, searchCount);
    }
}
