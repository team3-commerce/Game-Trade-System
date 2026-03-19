package com.example.tradedemo.auth.controller;

import static com.example.tradedemo.auth.consts.AuthConst.BEARER_PREFIX;

import com.example.tradedemo.auth.dto.*;
import com.example.tradedemo.auth.service.AuthService;
import com.example.tradedemo.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/v1/auth/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupAuthRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    @PostMapping("/v1/auth/login")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> login(@Valid @RequestBody LoginAuthRequest request) {
        TokenAuthResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("200", tokenResponse));
    }

    @PostMapping("/v1/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    /**
     * 로그인 V2
     */
    @PostMapping("/v2/auth/login")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> loginV2(@Valid @RequestBody LoginAuthRequest request) {
        TokenAuthResponse tokenResponse = authService.loginV2(request);
        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK), tokenResponse));
    }

    /**
     * 로그인 V3
     */
    @PostMapping("/v3/auth/login")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> loginV3(@Valid @RequestBody LoginAuthRequest request) {
        TokenAuthResponse tokenResponse = authService.loginV3(request);
        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK), tokenResponse));
    }

    /**
     * 로그아웃 V2
     */
    @PostMapping("/v2/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logoutV2(
            @AuthenticationPrincipal PrincipalDetails principalDetails, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String accessToken = (authHeader != null && authHeader.startsWith(BEARER_PREFIX))
                ? authHeader.substring(BEARER_PREFIX.length())
                : null;

        authService.logoutV2(principalDetails.getEmail(), accessToken);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    /**
     * 로그아웃 V3
     */
    @PostMapping("/v3/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logoutV3(
            @AuthenticationPrincipal PrincipalDetails principalDetails, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String accessToken = (authHeader != null && authHeader.startsWith(BEARER_PREFIX))
                ? authHeader.substring(BEARER_PREFIX.length())
                : null;

        authService.logoutV3(principalDetails.getEmail(), accessToken);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    /**
     * 토큰 재발급 V2
     */
    @PostMapping("/v2/auth/reissue")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> reissueV2(@RequestBody TokenAuthResponse tokenAuthRequest) {
        TokenAuthResponse tokenResponse =
                authService.reissueV2(tokenAuthRequest.refreshToken());
        return ResponseEntity.ok(ApiResponse.success("200", tokenResponse));
    }

    /**
     * 토큰 재발급 V3
     */
    @PostMapping("/v3/auth/reissue")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> reissueV3(@RequestBody TokenAuthResponse tokenAuthRequest) {
        TokenAuthResponse tokenResponse =
                authService.reissueV3(tokenAuthRequest.refreshToken());
        return ResponseEntity.ok(ApiResponse.success("200", tokenResponse));
    }

    /**
     * 소셜 가입자 비밀번호 설정 (V2)
     */
    @PatchMapping("/v2/auth/password")
    public ResponseEntity<ApiResponse<Void>> setPassword(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody SetPasswordRequest request) {
        authService.setPassword(principalDetails.getEmail(), request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    /**
     * 소셜 연동 해제 (V2)
     */
    @DeleteMapping("/v2/auth/social")
    public ResponseEntity<ApiResponse<Void>> unlinkSocial(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody UnlinkSocialRequest request) {
        authService.unlinkSocial(principalDetails.getEmail(), request);
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
}
