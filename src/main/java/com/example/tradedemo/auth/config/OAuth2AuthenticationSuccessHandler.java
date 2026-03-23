package com.example.tradedemo.auth.config;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.auth.dto.TokenAuthResponse;
import com.example.tradedemo.auth.service.AuthService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();

        TokenAuthResponse tokens = authService.createTokens(member.getEmail(), member.getRole().name());

        // 소셜 로그인은 통합 계정 로직을 따르므로, 
        // V2(Local Cache)와 V3(Redis) 환경 모두에서 reissue가 가능하도록 두 저장소에 모두 저장
        authService.saveRefreshTokenToCache(member.getEmail(), tokens.refreshToken());
        authService.saveRefreshTokenToRedis(member.getEmail(), tokens.refreshToken());

        member.updateLastLoginAt();
        memberRepository.save(member);

        log.info("Social login success for user: {}", member.getEmail());

        String targetUrl = UriComponentsBuilder.fromUriString("/api/auth/oauth-success")
                .queryParam("accessToken", tokens.accessToken())
                .queryParam("refreshToken", tokens.refreshToken())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
