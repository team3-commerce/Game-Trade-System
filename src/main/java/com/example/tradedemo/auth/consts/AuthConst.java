package com.example.tradedemo.auth.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConst {

    public static final String AUTHENTICATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // 토큰 만료 시간
    public static final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 30; // 30분
    public static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7; // 7일

    // 캐시 이름
    public static final String BLACKLIST_CACHE_NAME = "blacklistedTokens";
}
