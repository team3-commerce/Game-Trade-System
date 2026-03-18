package com.example.tradedemo.domain.pending.service;


import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.pending.repository.PendingAssetRedisLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PendingAssetLockService {

    private final PendingAssetRedisLockRepository pendingAssetRedisLockRepositoryLockRedisRepository;


    private static final Duration LOCK_TTL         = Duration.ofSeconds(5);
    private static final int      MAX_RETRY         = 10;
    private static final long     RETRY_INTERVAL_MS = 100L;

    /**
     * 락을 획득하고 실행 후 락 해제
     * 실패 시 100ms 간격으로 최대 10회 재시도
     * @param lockKey 락 키 (예: "pending-asset:1")
     * @param action  락 안에서 실행할 비즈니스 로직
     * executeWithLock(lockKey, () -> {action});
     */
    public void executeWithLock(String lockKey, Runnable action) {
        String lockValue = UUID.randomUUID().toString();

        try {
            acquire(lockKey, lockValue);
            action.run();
        } finally {
            pendingAssetRedisLockRepositoryLockRedisRepository.unlock(lockKey, lockValue);
        }
    }

    private void acquire(String lockKey, String lockValue) {
        for (int i = 0; i < MAX_RETRY; i++) {
            if (pendingAssetRedisLockRepositoryLockRedisRepository.tryLock(lockKey, lockValue, LOCK_TTL)) {
                return;
            }
            try {
                Thread.sleep(RETRY_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ServiceException(ErrorEnum.ERR_LOCK_INTERRUPTED); // 500, 예기치 못한 일 발생
            }
        }
        throw new ServiceException(ErrorEnum.ERR_LOCK_ACQUIRE_FAILED); // 409 요청이 현재 서버 상태와 충돌이 발생하여 처리 불가
    }
}