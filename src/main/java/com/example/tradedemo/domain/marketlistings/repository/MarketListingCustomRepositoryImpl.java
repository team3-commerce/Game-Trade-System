package com.example.tradedemo.domain.marketlistings.repository;

import static com.example.tradedemo.domain.marketlistings.entity.QMarketListing.marketListing;

import com.example.tradedemo.domain.marketlistings.dto.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class MarketListingCustomRepositoryImpl implements MarketListingCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchAllMarketListingResponse> getAllMarketListingWithKeyword(
            Long memberId,
            String keyword,
            MarketListingStatus listingStatus,
            String sortTotalPrice,
            String sortSaleEndAt,
            Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        /**
         *  where절 검색 조건 설정
         */
        if (keyword != null && !keyword.isBlank()) {
            builder.and(marketListing.itemName.startsWithIgnoreCase(keyword));
        }

        if (memberId != null) {
            builder.and(marketListing.member.id.eq(memberId));
        }

        if (listingStatus != null) {
            builder.and(marketListing.status.eq(listingStatus));
        }

        /**
         *  order by절 정렬조건 설정
         */
        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(sortTotalPrice, sortSaleEndAt);

        List<SearchAllMarketListingResponse> content = queryFactory
                .select(Projections.constructor(
                        SearchAllMarketListingResponse.class,
                        marketListing.id,
                        marketListing.itemName,
                        marketListing.totalPrice,
                        marketListing.quantity,
                        marketListing.status,
                        marketListing.saleEndAt,
                        marketListing.createdAt,
                        marketListing.modifiedAt))
                .from(marketListing)
                .where(builder)
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(marketListing.count())
                .from(marketListing)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(String sortTotalPrice, String sortSaleEndAt) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if ("asc".equalsIgnoreCase(sortTotalPrice)) {
            orderSpecifiers.add(marketListing.totalPrice.asc());
        } else if ("desc".equalsIgnoreCase(sortTotalPrice)) {
            orderSpecifiers.add(marketListing.totalPrice.desc());
        }

        if ("asc".equalsIgnoreCase(sortSaleEndAt)) {
            orderSpecifiers.add(marketListing.saleEndAt.asc());
        } else if ("desc".equalsIgnoreCase(sortSaleEndAt)) {
            orderSpecifiers.add(marketListing.saleEndAt.desc());
        }

        orderSpecifiers.add(marketListing.createdAt.desc());

        return orderSpecifiers;
    }
}
