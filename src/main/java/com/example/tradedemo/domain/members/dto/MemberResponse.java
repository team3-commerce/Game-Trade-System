package com.example.tradedemo.domain.members.dto;

import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberRole;

public record MemberResponse(String email, String nickname, MemberRole role) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getEmail(), member.getNickname(), member.getRole());
    }
}
