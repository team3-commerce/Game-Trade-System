package com.example.tradedemo.auth.controller;

import com.example.tradedemo.auth.dto.LoginAuthRequest;
import com.example.tradedemo.auth.dto.SignupAuthRequest;
import com.example.tradedemo.auth.dto.TokenAuthResponse;
import com.example.tradedemo.auth.service.AuthService;
import com.example.tradedemo.common.dto.ApiResponse;
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
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupAuthRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenAuthResponse>> login(@Valid @RequestBody LoginAuthRequest request) {
        TokenAuthResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("200", tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("200", null));
    }
}
