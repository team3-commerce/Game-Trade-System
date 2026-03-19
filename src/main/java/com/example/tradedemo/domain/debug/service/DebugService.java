package com.example.tradedemo.domain.debug.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.debug.dto.GiveMemberItemRequest;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import com.example.tradedemo.domain.members.dto.GetMemberItemResponse;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.tradedemo.domain.members.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Profile("!prod")
@ConditionalOnProperty(name = "app.debug-api.enabled", havingValue = "true")
public class DebugService {
    private final MemberRepository memberRepository;
    private final MemberItemRepository memberItemRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public GetMemberItemResponse giveMemberItem(GiveMemberItemRequest req) {
        Member member = memberRepository
            .findByEmail(req.getMemberEmail()).orElseThrow(
                    () -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND)
            );

        MemberItem memberItem = memberItemRepository
                .findByMemberIdAndItemId(member.getId(), req.getItemId())
                .orElse(null);

        if (memberItem != null) {
            memberItem.increase(req.getQuantity());
        } else {
            LocalDateTime acquiredAt = req.getAcquiredAt();

            if (acquiredAt == null) {
                acquiredAt = LocalDateTime.now();
            }

            Item item = itemRepository
                    .findById(req.getItemId())
                    .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_ITEM_NOT_FOUND));

            memberItem = MemberItem.create(member, item, acquiredAt, req.getQuantity());
        }

        memberItem = memberItemRepository.saveAndFlush(memberItem);

        return new GetMemberItemResponse(
                memberItem.getId(),
                memberItem.getItem().getName(),
                memberItem.getQuantity(),
                memberItem.getAcquiredAt(),
                memberItem.getCreatedAt(),
                memberItem.getModifiedAt());
    }
}
