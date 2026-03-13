package com.example.tradedemo.auth.dto;

public record TokenAuthResponse(String accessToken, String refreshToken, String granType) {
    public TokenAuthResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}
