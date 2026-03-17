package com.example.tradedemo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptimisticLock {

    // 최대 재시도 횟수
    int maxRetry() default 50;

    // 재시도 시 대기 시간
    long retryDelayMs() default 50;
}
