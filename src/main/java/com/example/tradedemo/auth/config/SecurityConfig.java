package com.example.tradedemo.auth.config;

import com.example.tradedemo.auth.filter.JwtAuthenticationFilter;
import com.example.tradedemo.auth.provider.JwtTokenProvider;
import com.example.tradedemo.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http, 
            JwtTokenProvider jwtTokenProvider, 
            CacheManager cacheManager,
            RedisTemplate<String, Object> redisTemplate) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(
                                        "/api/v1/auth/**",
                                        "/api/v2/auth/**",
                                        "/api/v3/auth/**",
                                        "/api/v2/auth/oauth-success",
                                        "/login/oauth2/**",
                                        "/oauth2/**"
                                )
                                .permitAll() // 화이트리스트
                                .requestMatchers("/api/v1/admin/**")
                                .hasRole("ADMIN") // 관리자 전용
                                .anyRequest()
                                .authenticated() // 나머지는 인증 필요
                        )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, cacheManager, redisTemplate),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exception -> exception
                                .authenticationEntryPoint(customAuthenticationEntryPoint) // 401 처리 등록
                                .accessDeniedHandler(customAccessDeniedHandler) // 403 처리 등록
                        );

        return http.build();
    }
}
