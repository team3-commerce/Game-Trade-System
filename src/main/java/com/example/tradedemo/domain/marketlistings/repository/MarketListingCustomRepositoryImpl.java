package com.example.tradedemo.domain.marketlistings.repository;

import com.example.tradedemo.domain.marketlistings.dto.response.SearchAllMarketListingResponse;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.example.tradedemo.domain.item.entity.QItem.item;
import static com.example.tradedemo.domain.marketlistings.entity.QMarketListing.marketListing;
import static com.example.tradedemo.domain.members.entity.QMemberItem.memberItem;

public class MarketListingCustomRepositoryImpl implements MarketListingCustomRepository {
    @Override
    public Page<SearchAllMarketListingResponse> getAllMarketListingWithKeyword(String keyword, Pageable pageable) {

        return null;
    }
}
