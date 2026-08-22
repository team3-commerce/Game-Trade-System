package com.example.tradedemo.auth.config;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.auth.provider.JwtTokenProvider;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final CacheManager cacheManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        // 캐시에 Refresh Token 저장
        Objects.requireNonNull(cacheManager.getCache("refreshTokens")).put(member.getEmail(), refreshToken);
        
        // 로그인 성공 시 기존 회원 인증 정보 캐시 삭제
        Objects.requireNonNull(cacheManager.getCache("memberAuths")).evict(member.getEmail());

        member.updateLastLoginAt();
        memberRepository.save(member);

        String targetUrl = UriComponentsBuilder.fromUriString("/api/v2/auth/oauth-success")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
