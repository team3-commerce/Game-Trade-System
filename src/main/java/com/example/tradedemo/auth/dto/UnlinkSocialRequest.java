package com.example.tradedemo.auth.dto;

import com.example.tradedemo.domain.members.enums.SocialProvider;
import jakarta.validation.constraints.NotNull;

public record UnlinkSocialRequest(
    @NotNull(message = "해제할 소셜 제공자를 선택해주세요")
    SocialProvider provider
) {}
