package com.example.tradedemo.domain.members.repository;

import static com.example.tradedemo.domain.item.entity.QItem.item;
import static com.example.tradedemo.domain.members.entity.QMemberItem.memberItem;

import com.example.tradedemo.domain.members.dto.GetAllMemberItemResponse;
import com.example.tradedemo.domain.members.dto.GetMemberItemResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class MemberItemCustomRepositoryImpl implements MemberItemCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<GetAllMemberItemResponse> findAllMemberItemByMemberId(Long memberId, Pageable pageable) {

        List<GetAllMemberItemResponse> content = queryFactory
                .select(Projections.constructor(
                        GetAllMemberItemResponse.class,
                        memberItem.id,
                        item.name,
                        memberItem.quantity,
                        memberItem.acquiredAt,
                        memberItem.createdAt,
                        memberItem.modifiedAt))
                .from(memberItem)
                .join(memberItem.item, item)
                .where(memberItem.member.id.eq(memberId))
                .orderBy(memberItem.acquiredAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(memberItem.id.count())
                .from(memberItem)
                .where(memberItem.member.id.eq(memberId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Optional<GetMemberItemResponse> findMemberItemByMemberIdAndMemberItemId(Long memberId, Long memberItemId) {

        GetMemberItemResponse result = queryFactory
                .select(Projections.constructor(
                        GetMemberItemResponse.class,
                        memberItem.id,
                        item.name,
                        memberItem.quantity,
                        memberItem.acquiredAt,
                        memberItem.createdAt,
                        memberItem.modifiedAt))
                .from(memberItem)
                .join(memberItem.item, item)
                .where(memberItem.member.id.eq(memberId), memberItem.id.eq(memberItemId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
