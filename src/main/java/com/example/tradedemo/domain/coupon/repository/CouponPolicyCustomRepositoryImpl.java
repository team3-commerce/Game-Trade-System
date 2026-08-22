package com.example.tradedemo.domain.coupon.repository;

import static com.example.tradedemo.domain.coupon.entity.QCouponPolicy.couponPolicy;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.coupon.dto.SearchAllCouponPolicyResponse;
import com.example.tradedemo.domain.coupon.entity.CouponPolicy;
import com.example.tradedemo.domain.coupon.enums.IssueType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CouponPolicyCustomRepositoryImpl implements CouponPolicyCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponse<SearchAllCouponPolicyResponse> getAllCouponPolicy(
            String sortCreatedAt, String issueType, Pageable pageable) {

        // issueType 필터
        BooleanBuilder builder = new BooleanBuilder();
        if (issueType != null && !issueType.isBlank()) {
            try {
                builder.and(couponPolicy.issueType.eq(IssueType.valueOf(issueType)));
            } catch (IllegalArgumentException ignored) {
            }
        }

        // 정렬 기본값: 최신순 desc
        OrderSpecifier<?> order =
                "asc".equalsIgnoreCase(sortCreatedAt) ? couponPolicy.createdAt.asc() : couponPolicy.createdAt.desc();

        List<CouponPolicy> content = queryFactory
                .selectFrom(couponPolicy)
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(couponPolicy.count())
                .from(couponPolicy)
                .where(builder)
                .fetchOne();

        return PageResponse.of(new PageImpl<>(
                content.stream().map(SearchAllCouponPolicyResponse::of).toList(), pageable, total == null ? 0 : total));
    }
}
