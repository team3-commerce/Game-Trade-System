package com.example.tradedemo.domain.members.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialProvider {
    GOOGLE("google"),
    KAKAO("kakao"),
    GITHUB("github");

    private final String registrationId;

    public static SocialProvider from(String registrationId) {
        for (SocialProvider provider : values()) {
            if (provider.registrationId.equalsIgnoreCase(registrationId)) {
                return provider;
            }
        }
        return null;
    }
}
