package com.example.tradedemo.domain.members.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberSuspendRequest(@NotBlank String email, @NotBlank String reason) {}
