package com.example.tradedemo.domain.members.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.members.consts.MemberConst;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.enums.MemberStatus;
import com.example.tradedemo.domain.members.enums.SocialProvider;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    // 소셜 계정 연동 목록
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocialAccount> socialAccounts = new ArrayList<>();

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

    // 소셜 전용 계정인지 확인 (비밀번호가 없으면 소셜 전용)
    public boolean isSocialOnly() {
        return this.password == null;
    }

    // 소셜 연동 해제
    public void unlinkSocial(SocialProvider provider) {
        // 해당 제공자의 연동 정보 찾기
        SocialAccount socialAccount = this.socialAccounts.stream()
                .filter(sa -> sa.getProvider().equals(provider))
                .findFirst()
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_SOCIAL_NOT_FOUND));

        // 최소 하나의 인증 수단(비밀번호 또는 다른 소셜 연동)이 유지되는지 검증
        boolean hasPassword = (this.password != null);
        long otherSocialCount = this.socialAccounts.stream()
                .filter(sa -> !sa.getProvider().equals(provider))
                .count();

        if (!hasPassword && otherSocialCount == 0) {
            // 다른 로그인 수단이 없으면 연동 해제 불가 (비밀번호 설정 유도 필요)
            throw new ServiceException(ErrorEnum.ERR_AUTH_SOCIAL_UNLINK_FORBIDDEN);
        }

        // 연동 정보 제거 (orphanRemoval로 DB에서 자동 삭제)
        this.socialAccounts.remove(socialAccount);
    }

    // 소셜 계정 추가 (연동)
    public void linkSocial(SocialProvider provider, String providerId) {
        // 이미 연동된 정보가 있는지 확인
        boolean alreadyLinked = this.socialAccounts.stream()
                .anyMatch(sa -> sa.getProvider().equals(provider));
        
        if (alreadyLinked) {
            return; // 혹은 이미 연동되었다는 예외 발생
        }

        this.socialAccounts.add(SocialAccount.create(this, provider, providerId));
    }
}
