package com.example.tradedemo.domain.pending.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PendingAssetRedisLockRepository {
    private final StringRedisTemplate stringRedisTemplate;

    // 내가 건 Redis 락 조회 확인 후 원자적(del)으로 삭제
    private static final String UNLOCK_SCRIPT =
         "if redis.call('get', KEYS[1]) == ARGV[1] then " +
         "    return redis.call('del', KEYS[1]) " +
         "else " +
         "    return 0 " +
         "end";

    /**
     * 락 획득 시도 (SETNX + TTL)
     * @param key   락 키
     * @param value 락 소유자 식별값 (UUID)
     * @param ttl   락 만료 시간
     * @return 획득 성공 여부
     */
     public boolean tryLock(String key, String value, Duration ttl) {
         return Boolean.TRUE.equals(
                 stringRedisTemplate.opsForValue()
                         .setIfAbsent(key, value, ttl)
         );
     }

     /**
      * 락 해제 (내 락만 해제_키,UUID로 확인함)
      * @param key   락 키
      * @param value 락 획득 시 사용한 UUID
      */
     public void unlock(String key, String value) {
         stringRedisTemplate.execute(
                 new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class),
                 List.of(key),
                 value
         );
     }
 }

