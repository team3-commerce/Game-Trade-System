package com.example.tradedemo.auth.service;

import static com.example.tradedemo.auth.consts.AuthConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.tradedemo.auth.dto.TokenAuthResponse;
import com.example.tradedemo.auth.provider.JwtTokenProvider;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.enums.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private Cache cache;

    @Test
    @DisplayName("비밀번호 암호화 성공")
    void encodePassword_success() {
        // given
        String password = "password";
        given(passwordEncoder.encode(password)).willReturn("encodedPassword");

        // when
        String result = authService.encodePassword(password);

        // then
        assertThat(result).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("토큰 생성 성공")
    void createTokens_success() {
        // given
        String email = "test@example.com";
        String role = "USER";
        given(jwtTokenProvider.createAccessToken(email, role)).willReturn("at");
        given(jwtTokenProvider.createRefreshToken(email)).willReturn("rt");

        // when
        TokenAuthResponse response = authService.createTokens(email, role);

        // then
        assertThat(response.accessToken()).isEqualTo("at");
        assertThat(response.refreshToken()).isEqualTo("rt");
    }

    @Test
    @DisplayName("리프레시 토큰 캐시 저장 성공")
    void saveRefreshTokenToCache_success() {
        // given
        String email = "test@example.com";
        String token = "token";
        given(cacheManager.getCache(REFRESH_TOKEN_CACHE_NAME)).willReturn(cache);

        // when
        authService.saveRefreshTokenToCache(email, token);

        // then
        verify(cache).put(email, token);
    }

    @Test
    @DisplayName("리프레시 토큰 Redis 저장 성공")
    void saveRefreshTokenToRedis_success() {
        // given
        String email = "test@example.com";
        String token = "token";
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        authService.saveRefreshTokenToRedis(email, token);

        // then
        verify(valueOperations).set(eq(V3_REFRESH_TOKEN_PREFIX + email), eq(token), eq(V3_REFRESH_TOKEN_TTL));
    }
}
