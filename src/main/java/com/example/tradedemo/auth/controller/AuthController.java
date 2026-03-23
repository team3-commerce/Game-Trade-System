package com.example.tradedemo.auth.controller;

import static com.example.tradedemo.auth.consts.AuthConst.AUTHENTICATION_HEADER;
import static com.example.tradedemo.auth.consts.AuthConst.BEARER_PREFIX;
import static com.example.tradedemo.auth.consts.AuthConst.SUCCESS_CODE;

import com.example.tradedemo.auth.dto.*;
import com.example.tradedemo.auth.facade.AuthFacade;
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

    private final AuthFacade authFacade;

    /**
     * 회원가입
     */
    @PostMapping("/v1/auth/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody @Valid SignupAuthRequest request) {
        authFacade.signup(request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    /**
     * 로그인
     */
    @PostMapping("/v1/auth/login")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> login(@RequestBody @Valid LoginAuthRequest request) {
        TokenAuthResponse response = authFacade.login(request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, response));
    }

    @PostMapping("/v2/auth/login")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> loginV2(@RequestBody @Valid LoginAuthRequest request) {
        TokenAuthResponse response = authFacade.loginV2(request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, response));
    }

    @PostMapping("/v3/auth/login")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> loginV3(@RequestBody @Valid LoginAuthRequest request) {
        TokenAuthResponse response = authFacade.loginV3(request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, response));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/v1/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authFacade.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @PostMapping("/v2/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logoutV2(
            @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        String accessToken = resolveToken(request);
        authFacade.logoutV2(userDetails.getUsername(), accessToken);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @PostMapping("/v3/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logoutV3(
            @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        String accessToken = resolveToken(request);
        authFacade.logoutV3(userDetails.getUsername(), accessToken);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/v1/auth/reissue")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> reissue(@RequestBody @Valid TokenReissueRequest request) {
        TokenAuthResponse response = authFacade.reissue(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, response));
    }

    @PostMapping("/v2/auth/reissue")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> reissueV2(@RequestBody @Valid TokenReissueRequest request) {
        TokenAuthResponse response = authFacade.reissueV2(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, response));
    }

    @PostMapping("/v3/auth/reissue")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> reissueV3(@RequestBody @Valid TokenReissueRequest request) {
        TokenAuthResponse response = authFacade.reissueV3(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, response));
    }

    /**
     * 소셜 가입자 비밀번호 설정
     */
    @PostMapping("/v2/auth/set-password")
    public ResponseEntity<ApiResponse<Void>> setPasswordV2(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid SetPasswordRequest request) {
        authFacade.setPassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @PostMapping("/v3/auth/set-password")
    public ResponseEntity<ApiResponse<Void>> setPasswordV3(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid SetPasswordRequest request) {
        authFacade.setPassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    /**
     * 소셜 연동 해제
     */
    @PostMapping("/v2/auth/unlink-social")
    public ResponseEntity<ApiResponse<Void>> unlinkSocialV2(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid UnlinkSocialRequest request) {
        authFacade.unlinkSocialV2(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    @PostMapping("/v3/auth/unlink-social")
    public ResponseEntity<ApiResponse<Void>> unlinkSocialV3(
            @AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid UnlinkSocialRequest request) {
        authFacade.unlinkSocialV3(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, null));
    }

    /**
     * 소셜 로그인 성공 후 토큰 확인용 임시 엔드포인트
     */
    @GetMapping("/auth/oauth-success")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> oauthSuccess(
            @RequestParam String accessToken, @RequestParam String refreshToken) {
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CODE, new TokenAuthResponse(accessToken, refreshToken)));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHENTICATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
