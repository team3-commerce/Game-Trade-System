package com.example.tradedemo.domain.coupon.service;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.coupon.constants.CouponCacheConst;
import com.example.tradedemo.domain.coupon.dto.SearchAllCouponHistoryResponse;
import com.example.tradedemo.domain.coupon.dto.SearchAllCouponPolicyResponse;
import com.example.tradedemo.domain.coupon.dto.SearchAllMemberCouponResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // 공통 SCAN 삭제 메서드
    private int evictByPattern(String pattern) {
        List<String> keys = new ArrayList<>();
        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.keyCommands().scan(
                    ScanOptions.scanOptions().match(pattern).count(100).build())) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            }
            return null;
        });
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return keys.size();
    }
    // 쿠폰 정책 목록 캐시
    public String couponPoliciesKey(int page, String sortCreatedAt, String issueType) {
        return CouponCacheConst.POLICIES_PREFIX + "page:" + page + ":sort:" + sortCreatedAt + ":type:" + issueType;
    }

    public PageResponse<SearchAllCouponPolicyResponse> getCouponPolicies(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;
        return objectMapper.convertValue(
                value,
                new TypeReference<PageResponse<SearchAllCouponPolicyResponse>>() {});
    }

    public void setCouponPolicies(String key, Object value) {
        redisTemplate.opsForValue().set(
                key, value, CouponCacheConst.COUPON_POLICIES_TTL_MINUTES, TimeUnit.MINUTES);
        log.info("[CouponCache] 쿠폰 정책 목록 캐시 저장 - key: {}", key);
    }

    // 정책 생성 시 "coupon:policies:*" 패턴의 모든 키 삭제
    // KEYS -> SCAN
    public void evictAllCouponPolicies() {
        int count = evictByPattern(CouponCacheConst.POLICIES_PREFIX + "*");
        log.debug("[CouponCache] 쿠폰 정책 목록 캐시 전체 삭제 - {}건", count);
    }


    // 내 쿠폰 전체 목록 캐시
    public String memberCouponsListKey(Long memberId, int page, String status) {
        return CouponCacheConst.COUPONS_PREFIX + memberId + ":coupons:page:" + page + ":status:" + status;
    }

    public PageResponse<SearchAllMemberCouponResponse> getMemberCouponsList(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;
        return objectMapper.convertValue(
                value,
                new TypeReference<PageResponse<SearchAllMemberCouponResponse>>() {});
    }

    public void setMemberCouponsList(String key, Object value) {
        redisTemplate.opsForValue().set(
                key, value, CouponCacheConst.MEMBER_COUPONS_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("[CouponCache] 내 쿠폰 목록 캐시 저장 - key: {}", key);
    }

    // 내 쿠폰 단건 캐시
    public String memberCouponItemKey(Long memberId, Long couponId) {
        return CouponCacheConst.COUPONS_PREFIX + memberId + ":coupon:" + couponId;
    }

    public SearchAllMemberCouponResponse getMemberCouponItem(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;
        return objectMapper.convertValue(
                value,
                new TypeReference<SearchAllMemberCouponResponse>() {});
    }

    public void setMemberCouponItem(String key, Object value) {
        redisTemplate.opsForValue().set(
                key, value, CouponCacheConst.MEMBER_COUPONS_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("[CouponCache] 내 쿠폰 단건 캐시 저장 - key: {}", key);
    }

    // 쿠폰 발급/사용 시 "coupon:member:{memberId}:*" 패턴의 모든 키 삭제
    // KEYS -> SCAN
    public void evictAllMemberCoupons(Long memberId) {
        int count = evictByPattern(CouponCacheConst.COUPONS_PREFIX + memberId + ":*");
        log.debug("[CouponCache] 회원 쿠폰 캐시 전체 삭제 - memberId: {}, {}건", memberId, count);
    }

    public void evictMemberCouponItem(Long memberId, Long couponId) {
        String key = memberCouponItemKey(memberId, couponId);
        redisTemplate.delete(key);
        log.debug("[CouponCache] 내 쿠폰 단건 캐시 삭제 - key: {}", key);
    }


    // 쿠폰 사용 내역 캐시
    public String couponHistoriesKey(Long memberId, int page, String status, String sortCreatedAt) {
        return CouponCacheConst.HISTORIES_PREFIX + memberId + ":page:" + page + ":status:" + status + ":sort:" + sortCreatedAt;
    }

    public PageResponse<SearchAllCouponHistoryResponse> getCouponHistories(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return null;
        return objectMapper.convertValue(
                value,
                new TypeReference<PageResponse<SearchAllCouponHistoryResponse>>() {});
    }

    public void setCouponHistories(String key, Object value) {
        redisTemplate.opsForValue().set(
                key, value, CouponCacheConst.COUPON_HISTORIES_TTL_MINUTES, TimeUnit.MINUTES);
        log.debug("[CouponCache] 쿠폰 내역 캐시 저장 - key: {}", key);
    }

    // 사용 시 호출 → "coupon:histories:member:{memberId}:*" 패턴의 모든 키 삭제
    public void evictAllCouponHistories(Long memberId) {
        int count = evictByPattern(CouponCacheConst.HISTORIES_PREFIX + memberId + ":*");
        log.debug("[CouponCache] 쿠폰 내역 캐시 전체 삭제 - memberId: {}, {}건", memberId, count);
    }

    public String couponRanOutKey(Long couponId) {
        return CouponCacheConst.RAN_OUT_PREFIX + couponId;
    }

    public void cacheCouponHasRanOut(Long couponId) {
        redisTemplate.opsForValue().set(
                couponRanOutKey(couponId), 
                Boolean.TRUE, 
                CouponCacheConst.COUPON_POLICIES_TTL_MINUTES, 
                TimeUnit.MINUTES
        );
    }

    public boolean checkIfCouponRanOut(Long couponId) {
        Object value = redisTemplate.opsForValue().get(couponRanOutKey(couponId));
        if (value == null) return false;
        return true;
    }
}
