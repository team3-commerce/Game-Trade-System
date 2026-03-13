package com.example.tradedemo.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginAuthRequest(@NotBlank String email, @NotBlank String password) {}
