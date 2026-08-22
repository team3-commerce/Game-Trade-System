package com.example.tradedemo.domain.members.service;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.members.dto.GetAllMemberItemResponse;
import com.example.tradedemo.domain.members.dto.GetMemberItemResponse;
import com.example.tradedemo.domain.members.exception.MemberItemNotFoundException;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberItemService {

    private final MemberItemRepository memberItemRepository;
    private final MemberItemCacheService memberItemCacheService;

    @Transactional(readOnly = true)
    public PageResponse<GetAllMemberItemResponse> getAllMemberItem(Long memberId, Pageable pageable) {

        return PageResponse.of(memberItemRepository.findAllMemberItemByMemberId(memberId, pageable));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "inventoryList", key = "'member:' + #memberId + ':page:' + #pageable.getPageNumber()")
    public PageResponse<GetAllMemberItemResponse> getAllMemberItemV2(Long memberId, Pageable pageable) {
        return PageResponse.of(memberItemRepository.findAllMemberItemByMemberId(memberId, pageable));
    }

    @Transactional(readOnly = true)
    public PageResponse<GetAllMemberItemResponse> getAllMemberItemV3(Long memberId, Pageable pageable) {

        // redis 캐시 확인
        String key = memberItemCacheService.getInventoryListKey(memberId, pageable);

        PageResponse<GetAllMemberItemResponse> cached = memberItemCacheService.getMemberItemList(key);

        // 캐시가 있을 경우 반환
        if(cached != null) {
            return cached;
        }

        // 캐시가 없으면 db에서 조회 후 redis에 저장
        PageResponse<GetAllMemberItemResponse> result = PageResponse.of(memberItemRepository.findAllMemberItemByMemberId(memberId, pageable));

        memberItemCacheService.setMemberItemList(key, result);

        return result;
    }

    @Transactional(readOnly = true)
    public GetMemberItemResponse getMemberItem(Long memberId, Long memberItemId) {

        return memberItemRepository
                .findMemberItemByMemberIdAndMemberItemId(memberId, memberItemId)
                .orElseThrow(MemberItemNotFoundException::new);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "inventoryItem", key = "'member:' + #memberId + ':item:' + #memberItemId")
    public GetMemberItemResponse getMemberItemV2(Long memberId, Long memberItemId) {
        return memberItemRepository
                .findMemberItemByMemberIdAndMemberItemId(memberId, memberItemId)
                .orElseThrow(MemberItemNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public GetMemberItemResponse getMemberItemV3(Long memberId, Long memberItemId) {

        String key = memberItemCacheService.getInventoryItemKey(memberId, memberItemId);

        GetMemberItemResponse cached = memberItemCacheService.getMemberItem(key);
        if(cached != null) {
            return cached;
        }

        GetMemberItemResponse result = memberItemRepository
                .findMemberItemByMemberIdAndMemberItemId(memberId, memberItemId)
                .orElseThrow(MemberItemNotFoundException::new);

        memberItemCacheService.setMemberItem(key, result);

        return result;
    }
}
