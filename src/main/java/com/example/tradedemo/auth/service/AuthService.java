package com.example.tradedemo.auth.service;

import static com.example.tradedemo.auth.consts.AuthConst.*;

import com.example.tradedemo.auth.dto.*;
import com.example.tradedemo.auth.provider.JwtTokenProvider;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 비밀번호 검증
     */
    public void validatePassword(String plainPassword, String encodedPassword) {
        if (encodedPassword == null || !passwordEncoder.matches(plainPassword, encodedPassword)) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_PASSWORD);
        }
    }

    /**
     * 비밀번호 암호화
     */
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 토큰 생성
     */
    public TokenAuthResponse createTokens(String email, String role) {
        String accessToken = jwtTokenProvider.createAccessToken(email, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        return new TokenAuthResponse(accessToken, refreshToken);
    }

    /**
     * 토큰 유효성 검증 및 이메일 추출
     */
    public String validateAndGetEmail(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_EXPIRED_TOKEN);
        }
        return jwtTokenProvider.getUserEmail(refreshToken);
    }

    /**
     * 캐시 내 리프레시 토큰 저장 (V2)
     */
    public void saveRefreshTokenToCache(String email, String refreshToken) {
        getRefreshCache().put(email, refreshToken);
    }

    /**
     * Redis 내 리프레시 토큰 저장 (V3)
     */
    public void saveRefreshTokenToRedis(String email, String refreshToken) {
        redisTemplate.opsForValue().set(V3_REFRESH_TOKEN_PREFIX + email, refreshToken, V3_REFRESH_TOKEN_TTL);
    }

    /**
     * 캐시 내 리프레시 토큰 조회 (V2)
     */
    public String getRefreshTokenFromCache(String email) {
        return getRefreshCache().get(email, String.class);
    }

    /**
     * 캐시 내 리프레시 토큰 삭제 (V2)
     */
    public void deleteRefreshTokenFromCache(String email) {
        getRefreshCache().evict(email);
    }

    /**
     * Redis 내 리프레시 토큰 조회 (V3)
     */
    public String getRefreshTokenFromRedis(String email) {
        return (String) redisTemplate.opsForValue().get(V3_REFRESH_TOKEN_PREFIX + email);
    }

    /**
     * 리프레시 토큰 무효화 (Redis V3)
     */
    public void deleteRefreshTokenFromRedis(String email) {
        redisTemplate.delete(V3_REFRESH_TOKEN_PREFIX + email);
    }

    /**
     * 블랙리스트 등록 (V2)
     */
    public void addToBlacklistCache(String accessToken) {
        Cache blacklistCache = cacheManager.getCache(BLACKLIST_CACHE_NAME);
        if (blacklistCache != null) {
            blacklistCache.put(accessToken, LOGOUT_VALUE);
        }
    }

    /**
     * 블랙리스트 등록 (V3)
     */
    public void addToBlacklistRedis(String accessToken) {
        redisTemplate.opsForValue().set(V3_BLACKLIST_TOKEN_PREFIX + accessToken, LOGOUT_VALUE, V3_BLACKLIST_TOKEN_TTL);
    }

    /**
     * Refresh Token 전용 캐시 저장소를 가져오는 로직
     */
    private Cache getRefreshCache() {
        return Objects.requireNonNull(cacheManager.getCache(REFRESH_TOKEN_CACHE_NAME),
                String.format(CACHE_MISSING_ERROR_MESSAGE, REFRESH_TOKEN_CACHE_NAME));
    }
}
