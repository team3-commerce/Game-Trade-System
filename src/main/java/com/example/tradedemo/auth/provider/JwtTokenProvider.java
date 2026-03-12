package com.example.tradedemo.auth.provider;

import static com.example.tradedemo.common.consts.AuthConstants.ACCESS_TOKEN_VALIDITY;
import static com.example.tradedemo.common.consts.AuthConstants.REFRESH_TOKEN_VALIDITY;

import com.example.tradedemo.auth.config.JwtProperties;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;
    private SecretKey key;

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(String email, String role) {
        return createToken(email, role, ACCESS_TOKEN_VALIDITY);
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken() {
        return createToken(null, null, REFRESH_TOKEN_VALIDITY);
    }

    /**
     * 토큰 생성
     */
    public String createToken(String email, String role, long validity) {
        Claims claims = Jwts.claims().subject(email).add("role", role).build();

        Date now = new Date();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validity))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 권한 정보 조회
     */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmail(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_TOKEN);
        }
    }
}
