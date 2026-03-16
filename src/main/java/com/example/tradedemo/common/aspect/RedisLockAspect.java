package com.example.tradedemo.common.aspect;

import com.example.tradedemo.common.annotation.RedisLock;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class RedisLockAspect {

    private final LockRedisRepository lockRedisRepository;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(redisLock)")
    public Object run(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {

        // SpEL로 동적 락 키 추출
        String lockKey   = resolveKey(joinPoint, redisLock.key());
        String lockValue = UUID.randomUUID().toString();

        log.info("[RedisLockAspect] 락 획득 시도 - key: {}", lockKey);

        // 락 획득
        acquireLock(lockKey, lockValue, redisLock.retryDelaySeconds(), redisLock.lockTimeoutSeconds());

        try {
            return joinPoint.proceed();
        } finally {
            // 락 해제
            releaseLock(lockKey, lockValue);
        }
    }

    /**
     * 락 획득 시도
     */
    private void acquireLock(String key, String lockValue, long retryDelaySeconds, long lockTimeoutSeconds) {
        long deadline = System.currentTimeMillis() + retryDelaySeconds * 1000; // 현재 시점 + 10초

        while (System.currentTimeMillis() < deadline) {
            boolean acquired = lockRedisRepository.tryLock(key, lockValue, lockTimeoutSeconds);

            if (acquired) {
                log.info("락 획득 성공 - key: {}, value: {}", key, lockValue);
                return;
            }

            log.info("락 대기 중 - key: {}", key);
            try {
                // 0.1초 간격으로 락 획득 재시도
                Thread.sleep(100);
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
    private void releaseLock(String lockKey, String lockValue) {
        boolean released = lockRedisRepository.unlock(lockKey, lockValue);
        if (released) {
            log.info("[RedisLockAspect] 락 해제 성공 - key: {}", lockKey);
        } else {
            log.warn("[RedisLockAspect] 락 해제 실패 - key: {}", lockKey);
        }
    }

    /**
     * SpEL로 Lock Key를 동적으로 추출
     */
    private String resolveKey(ProceedingJoinPoint joinPoint, String redisLockKey) {
        // joinPoint의 메서드 정보 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 메서드 파라미터명 배열로 가져오기
        String[] paramNames = signature.getParameterNames();

        // 메서드 실제 인자 값 배열로 가져오기
        Object[] args = joinPoint.getArgs();

        // SpEL 컨텍스트에 파라미터명 → 실제 값으로 바인딩
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        // SpEL 표현식 → 최종 락 키 문자열 반환
        return parser.parseExpression(redisLockKey).getValue(context, String.class);
    }

}
