package com.example.tradedemo.domain.coupon.repository;

import static com.example.tradedemo.domain.coupon.entity.QCouponPolicy.couponPolicy;
import static com.example.tradedemo.domain.coupon.entity.QMemberCoupon.memberCoupon;

import com.example.tradedemo.domain.coupon.dto.SearchAllMemberCouponResponse;
import com.example.tradedemo.domain.coupon.entity.MemberCoupon;
import com.example.tradedemo.domain.coupon.enums.CouponStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class MemberCouponCustomRepositoryImpl implements MemberCouponCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchAllMemberCouponResponse> findAllMemberCouponByMemberId(
            Long memberId, String status, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(memberCoupon.member.id.eq(memberId));

        // status 필터
        if (status != null && !status.isBlank()) {
            try {
                builder.and(memberCoupon.status.eq(CouponStatus.valueOf(status)));
            } catch (IllegalArgumentException ignored) {
            }
        }

        List<MemberCoupon> content = queryFactory
                .selectFrom(memberCoupon)
                .join(memberCoupon.couponPolicy, couponPolicy)
                .fetchJoin()
                .where(builder)
                .orderBy(memberCoupon.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(memberCoupon.count())
                .from(memberCoupon)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(
                content.stream().map(SearchAllMemberCouponResponse::of).toList(), pageable, total == null ? 0 : total);
    }

    @Override
    public Optional<SearchAllMemberCouponResponse> findMemberCouponByMemberIdAndMemberCouponId(
            Long memberId, Long couponId) {

        MemberCoupon result = queryFactory
                .selectFrom(memberCoupon)
                .join(memberCoupon.couponPolicy, couponPolicy)
                .fetchJoin()
                .where(memberCoupon.member.id.eq(memberId), memberCoupon.id.eq(couponId))
                .fetchOne();

        return Optional.ofNullable(result).map(SearchAllMemberCouponResponse::of);
    }
}
