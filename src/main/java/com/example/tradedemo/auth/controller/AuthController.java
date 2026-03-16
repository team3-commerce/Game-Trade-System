package com.example.tradedemo.auth.controller;

import static com.example.tradedemo.auth.consts.AuthConst.BEARER_PREFIX;

import com.example.tradedemo.auth.dto.LoginAuthRequest;
import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.auth.dto.SignupAuthRequest;
import com.example.tradedemo.auth.dto.TokenAuthResponse;
import com.example.tradedemo.auth.service.AuthService;
import com.example.tradedemo.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(ApiResponse.success("200", tokenResponse));
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
     * 토큰 재발급 V2
     */
    @PostMapping("/v2/auth/reissue")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> reissueV2(
            @AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody TokenAuthResponse tokenAuthRequest) {
        TokenAuthResponse tokenResponse =
                authService.reissueV2(principalDetails.getEmail(), tokenAuthRequest.refreshToken());
        return ResponseEntity.ok(ApiResponse.success("200", tokenResponse));
    }
}
