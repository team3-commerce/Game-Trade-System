package com.example.tradedemo.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        // marketListings 캐시
        CaffeineCache marketListingsCache = new CaffeineCache(
                "marketListings",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .build());

        // member 캐시
        CaffeineCache memberCache = new CaffeineCache(
                "member",
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterAccess(Duration.ofMinutes(30))
                        .build());

        // memberAuth 캐시 (UserDetails/PrincipalDetails)
        CaffeineCache memberAuthCache = new CaffeineCache(
                "memberAuth",
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterAccess(Duration.ofMinutes(30))
                        .build());

        cacheManager.setCaches(List.of(marketListingsCache, memberCache, memberAuthCache));
        return cacheManager;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        JacksonJsonRedisSerializer<Object> serializer = new JacksonJsonRedisSerializer<>(Object.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
}
