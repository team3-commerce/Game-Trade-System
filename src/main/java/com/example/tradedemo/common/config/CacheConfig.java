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

        // members 캐시
        CaffeineCache membersCache = new CaffeineCache(
                "members",
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterAccess(Duration.ofMinutes(30))
                        .build());

        // memberAuths 캐시 (UserDetails/PrincipalDetails)
        CaffeineCache memberAuthsCache = new CaffeineCache(
                "memberAuths",
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterAccess(Duration.ofMinutes(30))
                        .build());

        // refreshTokens 캐시
        CaffeineCache refreshTokensCache = new CaffeineCache(
                "refreshTokens",
                Caffeine.newBuilder()
                        .maximumSize(10000)
                        .expireAfterWrite(Duration.ofDays(7).plusHours(1))
                        .build());

        // blacklistedTokens 캐시
        CaffeineCache blacklistedTokensCache = new CaffeineCache(
                "blacklistedTokens",
                Caffeine.newBuilder()
                        .maximumSize(10000)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .build());

        // couponPolicies 캐시
        CaffeineCache couponPoliciesCache = new CaffeineCache(
                "couponPolicies",
                Caffeine.newBuilder()
                        .maximumSize(50)
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .build());

        // memberCoupons 캐시
        CaffeineCache memberCouponsCache = new CaffeineCache(
                "memberCoupons",
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .build());

        // couponHistories 캐시
        CaffeineCache couponHistoriesCache = new CaffeineCache(
                "couponHistories",
                Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .build());

        cacheManager.setCaches(List.of(
                marketListingsCache,
                membersCache,
                memberAuthsCache,
                refreshTokensCache,
                blacklistedTokensCache,
                couponPoliciesCache,
                memberCouponsCache,
                couponHistoriesCache));
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
