package com.example.tradedemo.domain.members.entity;

import com.example.tradedemo.common.entity.Base;
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

    @Column(nullable = false)
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

    private Member(String email, String password, String nickname, MemberRole role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.status = MemberStatus.ACTIVE;
        this.statusChangedAt = LocalDateTime.now();
        this.statusReason = "신규 회원 가입";
    }

    public static Member create(String email, String password, String nickname, MemberRole role) {
        return new Member(email, password, nickname, role);
    }

    // 회원 탈퇴 (소프트 삭제)
    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
        this.statusChangedAt = LocalDateTime.now();
        this.statusReason = "사용자 요청에 의한 회원 탈퇴";
    }

    // 비활성화 처리
    public void deactivate(String reason) {
        this.status = MemberStatus.INACTIVE;
        this.statusChangedAt = LocalDateTime.now();
        this.statusReason = reason;
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
