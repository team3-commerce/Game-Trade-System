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
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

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
    public void evictAllCouponPolicies() {
        var keys = redisTemplate.keys(CouponCacheConst.POLICIES_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("[CouponCache] 쿠폰 정책 목록 캐시 전체 삭제 - {}건", keys.size());
        }
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
    public void evictAllMemberCoupons(Long memberId) {
        var keys = redisTemplate.keys(CouponCacheConst.COUPONS_PREFIX + memberId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("[CouponCache] 회원 쿠폰 캐시 전체 삭제 - memberId: {}, {}건", memberId, keys.size());
        }
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
        var keys = redisTemplate.keys(CouponCacheConst.HISTORIES_PREFIX + memberId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("[CouponCache] 쿠폰 내역 캐시 전체 삭제 - memberId: {}, {}건", memberId, keys.size());
        }
    }

}
