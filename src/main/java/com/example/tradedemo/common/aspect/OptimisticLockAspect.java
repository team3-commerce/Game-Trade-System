package com.example.tradedemo.common.aspect;

import com.example.tradedemo.common.annotation.OptimisticLock;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@Order(2)
public class OptimisticLockAspect {

    @Around("@annotation(optimisticLock)")
    public Object around(ProceedingJoinPoint joinPoint, OptimisticLock optimisticLock) throws Throwable {

        int maxRetry = optimisticLock.maxRetry();
        long retryDelayMs = optimisticLock.retryDelayMs();

        for (int attempt = 1; attempt <= maxRetry; attempt++) {
            try {

                return joinPoint.proceed();

            } catch (OptimisticLockException e) {

                if (attempt == maxRetry) {
                    log.error("[OptimisticLockRetry] 최대 재시도 횟수 초과 ({}/{})", attempt, maxRetry);
                    throw e;
                }

                log.warn("[OptimisticLockRetry] 버전 충돌 감지, 재시도 중 ({}/{}) - {}ms 대기",
                        attempt, maxRetry, retryDelayMs);

                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("낙관적 락 재시도 중 인터럽트 발생", ie);
                }
            }
        }

        throw new IllegalStateException("낙관적 락 재시도 로직 오류");
    }
}
