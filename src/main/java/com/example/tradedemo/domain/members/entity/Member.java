package com.example.tradedemo.domain.members.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.members.consts.MemberConst;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.enums.MemberStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 엔티티
 *
 */
@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Column(nullable = false)
    private LocalDateTime statusChangedAt;

    private String statusReason;

    private LocalDateTime lastLoginAt;

    private Member(String email, String password, String nickname, MemberRole role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.status = MemberStatus.ACTIVE;
        this.statusChangedAt = LocalDateTime.now();
        this.statusReason = MemberConst.REASON_SIGNUP;
    }

    public static Member create(String email, String password, String nickname, MemberRole role) {
        return new Member(email, password, nickname, role);
    }

    public static Member createSocial(String email, String nickname, MemberRole role) {
        return new Member(email, null, nickname, role);
    }
    
    public static Member createAuthMember(String email, MemberRole role) {
        Member member = new Member();
        member.email = email;
        member.role = role;
        return member;
    }

    // 로그인 시 시간 업데이트
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    // 회원 탈퇴 (소프트 삭제)
    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
        this.statusChangedAt = LocalDateTime.now();
        this.statusReason = MemberConst.REASON_WITHDRAWAL;
    }

    // 휴면 처리
    public void makeDormant() {
        this.status = MemberStatus.INACTIVE_DORMANT;
        this.statusChangedAt = LocalDateTime.now();
        this.statusReason = MemberConst.REASON_DORMANT;
    }

    // 관리자에 의한 계정 정지
    public void suspend(String reason) {
        this.status = MemberStatus.INACTIVE_SUSPENDED;
        this.statusChangedAt = LocalDateTime.now();
        this.statusReason = reason;
    }

    // 계정 복구 (휴면 해제)
    public void activate() {
        this.status = MemberStatus.ACTIVE;
        this.statusChangedAt = LocalDateTime.now();
        this.statusReason = MemberConst.REASON_ACTIVATE;
    }

    // Refresh Token 업데이트
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // 로그아웃 시 토큰 제거
    public void clearRefreshToken() {
        this.refreshToken = null;
    }

    // 비밀번호 변경
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 닉네임 변경
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
