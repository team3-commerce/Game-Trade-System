package com.example.tradedemo.auth.dto;

public record TokenAuthResponse(String accessToken, String refreshToken, String grantType) {
    public TokenAuthResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}
