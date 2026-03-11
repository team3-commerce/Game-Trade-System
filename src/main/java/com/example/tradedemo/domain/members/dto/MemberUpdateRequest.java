package com.example.tradedemo.domain.members.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberUpdateRequest(String nickname, @NotBlank String currentPassword, @NotBlank String newPassword) {}
