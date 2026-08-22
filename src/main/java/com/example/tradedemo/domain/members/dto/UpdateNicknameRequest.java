package com.example.tradedemo.domain.members.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateNicknameRequest(@NotBlank String nickname) {}
