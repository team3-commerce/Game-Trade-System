package com.example.tradedemo.auth.controller;

import static com.example.tradedemo.auth.consts.AuthConst.BEARER_PREFIX;

import com.example.tradedemo.auth.dto.*;
import com.example.tradedemo.auth.service.AuthService;
import com.example.tradedemo.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     */
    @PostMapping("/v1/auth/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid SignupAuthRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    /**
     * 로그인
     */
    @PostMapping("/v1/auth/login")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> login(@RequestBody @Valid LoginAuthRequest request) {
        TokenAuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("200", response));
    }

    @PostMapping("/v2/auth/login")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> loginV2(@RequestBody @Valid LoginAuthRequest request) {
        TokenAuthResponse response = authService.loginV2(request);
        return ResponseEntity.ok(ApiResponse.success("200", response));
    }

    @PostMapping("/v3/auth/login")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> loginV3(@RequestBody @Valid LoginAuthRequest request) {
        TokenAuthResponse response = authService.loginV3(request);
        return ResponseEntity.ok(ApiResponse.success("200", response));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/v1/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    @PostMapping("/v2/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logoutV2(
            @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        String accessToken = resolveToken(request);
        authService.logoutV2(userDetails.getUsername(), accessToken);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    @PostMapping("/v3/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logoutV3(
            @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        String accessToken = resolveToken(request);
        authService.logoutV3(userDetails.getUsername(), accessToken);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/v1/auth/reissue")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> reissue(@RequestBody @Valid TokenReissueRequest request) {
        TokenAuthResponse response = authService.reissue(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success("200", response));
    }

    @PostMapping("/v2/auth/reissue")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> reissueV2(@RequestBody @Valid TokenReissueRequest request) {
        TokenAuthResponse response = authService.reissueV2(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success("200", response));
    }

    @PostMapping("/v3/auth/reissue")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> reissueV3(@RequestBody @Valid TokenReissueRequest request) {
        TokenAuthResponse response = authService.reissueV3(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success("200", response));
    }

    /**
     * 소셜 가입자 비밀번호 설정
     */
    @PostMapping("/v2/auth/set-password")
    public ResponseEntity<ApiResponse<Void>> setPasswordV2(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid SetPasswordRequest request) {
        authService.setPasswordV2(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    @PostMapping("/v3/auth/set-password")
    public ResponseEntity<ApiResponse<Void>> setPasswordV3(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid SetPasswordRequest request) {
        authService.setPasswordV3(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    /**
     * 소셜 연동 해제
     */
    @PostMapping("/v2/auth/unlink-social")
    public ResponseEntity<ApiResponse<Void>> unlinkSocialV2(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid UnlinkSocialRequest request) {
        authService.unlinkSocialV2(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    @PostMapping("/v3/auth/unlink-social")
    public ResponseEntity<ApiResponse<Void>> unlinkSocialV3(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid UnlinkSocialRequest request) {
        authService.unlinkSocialV3(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    /**
     * 소셜 로그인 성공 후 토큰 확인용 임시 엔드포인트
     */
    @GetMapping("/v2/auth/oauth-success")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> oauthSuccess(
            @RequestParam String accessToken, @RequestParam String refreshToken) {
        return ResponseEntity.ok(ApiResponse.success("200", new TokenAuthResponse(accessToken, refreshToken)));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
