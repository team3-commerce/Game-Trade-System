package com.example.tradedemo.common.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@RequiredArgsConstructor
public class RedisCacheInitializer {

    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * 애플리케이션 실행 시 redis 캐시 삭제(local 환경에서만 실행)
     */
    @Bean
    @Profile("local")
    public ApplicationRunner initRedisCache(){
        return args -> {
            redisConnectionFactory.getConnection().serverCommands().flushDb();
        };
    }
}
