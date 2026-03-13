package com.example.tradedemo.domain.members.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {}
