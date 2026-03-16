package com.example.tradedemo.domain.coupon.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class LockService {

    private final LockRedisRepository lockRedisRepository;

    private static final long LOCK_TIMEOUT_SECONDS = 5;
    private static final int WAIT_SECONDS = 20;
    private static final long RETRY_DELAY_MS = 100;

    /**
     * 락 획득 시도
     * 실패 시 재시도 전략
     */
    public String acquireLock(String key) {
        String lockValue = java.util.UUID.randomUUID().toString();

        // 현재 시각 + 20초 = 마감 시각
        long deadline = System.currentTimeMillis() + WAIT_SECONDS * 1000;

        while (System.currentTimeMillis() < deadline) {
            boolean acquired = lockRedisRepository.tryLock(key, lockValue, LOCK_TIMEOUT_SECONDS);

            if (acquired) {
                log.info("락 획득 성공 - key: {}, value: {}", key, lockValue);
                return lockValue;
            }

            log.info("락 대기 중 - key: {}", key);
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("락 획득 중 인터럽트 발생", e);
            }
        }

        // 대기 시간 초과
        throw new ServiceException(ErrorEnum.ERR_COUPON_LOCK_CONFLICT);
    }

    /**
     * 락 해제
     */
    public void releaseLock(String key, String lockValue) {
        boolean released = lockRedisRepository.unlock(key, lockValue);
        if (released) {
            log.info("락 해제 성공 - key: {}", key);
        } else {
            log.warn("락 해제 실패 - key: {}", key);
        }
    }

    public String buildLockKey(Long couponPolicyId) {
        return "lock:coupon:" + couponPolicyId;
    }
}
