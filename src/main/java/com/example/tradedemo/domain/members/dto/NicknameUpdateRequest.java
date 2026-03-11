package com.example.tradedemo.domain.members.dto;

import jakarta.validation.constraints.NotBlank;

public record NicknameUpdateRequest(@NotBlank String nickname) {}
