package com.example.tradedemo.domain.members.service;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.members.consts.MemberItemConst;
import com.example.tradedemo.domain.members.dto.GetAllMemberItemResponse;
import com.example.tradedemo.domain.members.dto.GetMemberItemResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MemberItemCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public PageResponse<GetAllMemberItemResponse> getMemberItemList(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null) return null;

        return objectMapper.convertValue(
                value,
                new TypeReference<PageResponse<GetAllMemberItemResponse>>() {}
        );
    }

    public GetMemberItemResponse getMemberItem(String key){
        Object value = redisTemplate.opsForValue().get(key);
        if(value == null) return null;

        return objectMapper.convertValue(
                value,
                new TypeReference<GetMemberItemResponse>() {}
        );
    }

    public void setMemberItemList(String key, PageResponse<GetAllMemberItemResponse> result) {
        redisTemplate.opsForValue().set(key, result, MemberItemConst.INVENTORY_LIST_TIME_LIMIT, TimeUnit.MINUTES);
    }

    public void setMemberItem(String key, GetMemberItemResponse result) {
        redisTemplate.opsForValue().set(key, result, MemberItemConst.INVENTORY_ITEM_TIME_LIMIT, TimeUnit.MINUTES);
    }

    public void deleteMemberItemList(Long memberId) {
        String pattern = MemberItemConst.MEMBER_INVENTORY + memberId + ":*";

        // 해당 패턴으로 시작하는 키 집합 반환
        Set<String> keys = redisTemplate.keys(pattern);

        // 해당 패턴으로 시작하는 키 삭제
        if(keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void deleteMemberItem(Long memberId, Long memberItemId) {
        String key = getInventoryItemKey(memberId, memberItemId);

        redisTemplate.delete(key);
    }

    public String getInventoryListKey(Long memberId, Pageable pageable) {
        return MemberItemConst.MEMBER_INVENTORY + memberId + MemberItemConst.PAGE_SEGMENT + pageable.getPageNumber();
    }

    public String getInventoryItemKey(Long memberId, Long memberItemId) {
        return MemberItemConst.MEMBER_INVENTORY_ITEM + MemberItemConst.MEMBER_SEGMENT + memberId + MemberItemConst.ITEM_SEGMENT + memberItemId;
    }
}
