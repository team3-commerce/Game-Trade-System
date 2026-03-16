package com.example.tradedemo.auth.dto;

import com.example.tradedemo.domain.members.enums.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupAuthRequest(
        @NotBlank String email, @NotBlank String password, @NotBlank String nickname, @NotNull MemberRole role) {}
