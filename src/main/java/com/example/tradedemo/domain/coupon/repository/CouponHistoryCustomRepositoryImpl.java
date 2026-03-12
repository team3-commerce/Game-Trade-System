package com.example.tradedemo.domain.coupon.repository;

import static com.example.tradedemo.domain.coupon.entity.QCouponHistory.couponHistory;
import static com.example.tradedemo.domain.coupon.entity.QCouponPolicy.couponPolicy;
import static com.example.tradedemo.domain.coupon.entity.QMemberCoupon.memberCoupon;

import com.example.tradedemo.domain.coupon.dto.SearchAllCouponHistoryResponse;
import com.example.tradedemo.domain.coupon.entity.CouponHistory;
import com.example.tradedemo.domain.coupon.enums.CouponHistoryStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CouponHistoryCustomRepositoryImpl implements CouponHistoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchAllCouponHistoryResponse> findAllCouponHistoryByMemberId(
            Long memberId, String status, String sortCreatedAt, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(couponHistory.member.id.eq(memberId));

        if (status != null && !status.isBlank()) {
            try {
                builder.and(couponHistory.status.eq(CouponHistoryStatus.valueOf(status)));
            } catch (IllegalArgumentException ignored) {
            }
        }

        // 정렬 기본값 desc
        OrderSpecifier<?> order =
                "asc".equalsIgnoreCase(sortCreatedAt) ? couponHistory.createdAt.asc() : couponHistory.createdAt.desc();

        List<CouponHistory> content = queryFactory
                .selectFrom(couponHistory)
                .join(couponHistory.memberCoupon, memberCoupon)
                .fetchJoin()
                .join(memberCoupon.couponPolicy, couponPolicy)
                .fetchJoin()
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(couponHistory.count())
                .from(couponHistory)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(
                content.stream().map(SearchAllCouponHistoryResponse::of).toList(), pageable, total == null ? 0 : total);
    }
}
