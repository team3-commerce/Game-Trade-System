package com.example.tradedemo.common.aspect;

import com.example.tradedemo.common.annotation.PessimisticLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@Order(2)
public class PessimisticLockAspect {

    @Around("@annotation(pessimisticLock)")
    public Object around(ProceedingJoinPoint joinPoint, PessimisticLock pessimisticLock) throws Throwable {

        String methodName = joinPoint.getSignature().getName();

        log.info("[PessimisticLock] 비관적 락 시작 - method: {}", methodName);

        try {
            Object result = joinPoint.proceed();
            log.info("[PessimisticLock] 비관적 락 종료 - method: {}", methodName);
            return result;

        } catch (Exception e) {
            log.warn("[PessimisticLock] 예외 발생 - method: {}, 원인: {}", methodName, e.getMessage());
            throw e;
        }
    }
}
