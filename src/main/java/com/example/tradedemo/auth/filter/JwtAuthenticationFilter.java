package com.example.tradedemo.auth.filter;

import static com.example.tradedemo.auth.consts.AuthConst.*;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.auth.provider.JwtTokenProvider;
import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.domain.members.enums.MemberStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 토큰 추출
            String token = resolveToken(request);

            // 유효성 검사 및 블랙리스트 확인
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                if (!isBlacklisted(token)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    
                    // 회원 상태 체크
                    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
                    if (principalDetails.getMember().getStatus() != MemberStatus.ACTIVE) {
                        sendErrorResponse(response, ErrorEnum.ERR_AUTH_NOT_ACTIVE_STATUS);
                        return;
                    }

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 에러 응답 전송
     */
    private void sendErrorResponse(HttpServletResponse response, ErrorEnum errorEnum) throws IOException {
        response.setStatus(errorEnum.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> apiResponse = ApiResponse.fail(
                String.valueOf(errorEnum.getHttpStatus().value()), 
                errorEnum.getErrorMessage()
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    /**
     * 블랙리스트 여부 확인
     */
    private boolean isBlacklisted(String token) {
        return isCaffeineBlacklisted(token) || isRedisBlacklisted(token);
    }

    private boolean isCaffeineBlacklisted(String token) {
        Cache blacklistCache = cacheManager.getCache(BLACKLIST_CACHE_NAME);
        return blacklistCache != null && blacklistCache.get(token) != null;
    }

    private boolean isRedisBlacklisted(String token) {
        return redisTemplate.hasKey(V3_BLACKLIST_TOKEN_PREFIX + token);
    }

    /**
     * 토큰 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHENTICATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
