package com.example.tradedemo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {

    String key();

    // 락 획득 최대 재시도 시간 10초
    long retryDelaySeconds() default 10;

    // 락 TTL 5초
    long lockTimeoutSeconds() default 5;
}
