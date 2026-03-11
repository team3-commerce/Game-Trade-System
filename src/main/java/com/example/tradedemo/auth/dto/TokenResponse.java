package com.example.tradedemo.auth.dto;

public record TokenResponse(String accessToken, String refreshToken, String granType) {
    public TokenResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}
