package com.example.tradedemo.domain.item.repository;

import static com.example.tradedemo.domain.item.entity.QItem.item;
import static com.example.tradedemo.domain.marketlistings.entity.QMarketListing.marketListing;

import com.example.tradedemo.domain.item.dto.SearchItemRequest;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.enums.ItemType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> searchItem(SearchItemRequest searchTerm) {
        Pageable pageable = PageRequest.of(searchTerm.getPage(), 10);

        List<Item> content = queryFactory
                .selectFrom(item)
                .where(itemNameContains(searchTerm.getNormalizedKeyword()), itemTypeEquals(searchTerm.getItemType()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(searchTerm).toArray(OrderSpecifier[]::new))
                .fetch();

        JPAQuery<Long> totalCount = queryFactory
                .select(item.count())
                .from(item)
                .where(itemNameContains(searchTerm.getNormalizedKeyword()), itemTypeEquals(searchTerm.getItemType()));

        return PageableExecutionUtils.getPage(content, pageable, () -> totalCount.fetchOne());
    }

    private BooleanExpression itemNameContains(@Nullable String name) {
        if (name != null) {
            return item.name.containsIgnoreCase(name);
        }
        return null;
    }

    private BooleanExpression itemTypeEquals(@Nullable ItemType type) {
        if (type != null) {
            return item.itemType.eq(type);
        }
        return null;
    }

    private List<OrderSpecifier<?>> getOrderSpecifiers(SearchItemRequest searchTerm) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (searchTerm.shouldSortCreatedAtAsc()) {
            orderSpecifiers.add(item.createdAt.asc());
        }else {
            orderSpecifiers.add(item.createdAt.desc());
        }

        return orderSpecifiers;
    }
}
