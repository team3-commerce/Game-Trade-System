package com.example.tradedemo.domain.members.dto;

import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.enums.MemberRole;

public record GetMyInfoResponse(String email, String nickname, MemberRole role) {
    public static GetMyInfoResponse from(Member member) {
        return new GetMyInfoResponse(member.getEmail(), member.getNickname(), member.getRole());
    }
}
