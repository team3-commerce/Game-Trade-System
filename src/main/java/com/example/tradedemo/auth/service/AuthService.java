package com.example.tradedemo.auth.service;

import com.example.tradedemo.auth.dto.*;
import com.example.tradedemo.auth.provider.JwtTokenProvider;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.service.CouponService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.SocialAccount;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.members.repository.SocialAccountRepository;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CouponService couponService;
    private final WalletRepository walletRepository;
    private final CacheManager cacheManager;

    /**
     * 회원가입
     */
    @Transactional
    public void signup(SignupAuthRequest request) {
        // 중복 체크
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_DUPLICATE_EMAIL);
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_DUPLICATE_NICKNAME);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.create(request.email(), encodedPassword, request.nickname(), request.role());

        memberRepository.save(member);

        // 지갑 생성
        walletRepository.save(Wallet.create(member, BigDecimal.ZERO));

        // 회원가입 쿠폰 자동 발급
        couponService.autoSignupCoupon(member);
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenAuthResponse login(LoginAuthRequest request) {
        // 사용자 확인
        Member member = memberRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        // 비밀번호 확인
        if (member.getPassword() == null || !passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_PASSWORD);
        }

        // 계정 상태별 처리
        handleMemberStatus(member);

        // 로그인 처리
        String accessToken = jwtTokenProvider.createAccessToken(
                member.getEmail(), member.getRole().name());

        String refreshToken = jwtTokenProvider.createRefreshToken();

        member.updateRefreshToken(refreshToken);
        member.updateLastLoginAt();

        return new TokenAuthResponse(accessToken, refreshToken);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String email) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));
        member.clearRefreshToken();
    }

    /**
     * 로그인 V2
     */
    @Transactional
    public TokenAuthResponse loginV2(LoginAuthRequest request) {
        // 사용자 확인
        Member member = memberRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        // 비밀번호가 없는 소셜 가입자인 경우 별도 에러 처리
        if (member.getPassword() == null) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_SOCIAL_ACCOUNT_ONLY);
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_PASSWORD);
        }

        // 계정 상태별 처리
        handleMemberStatus(member);

        // 로그인 처리
        String accessToken = jwtTokenProvider.createAccessToken(
                member.getEmail(), member.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // 캐시에 저장
        getRefreshCache().put(member.getEmail(), refreshToken);

        member.updateLastLoginAt();

        return new TokenAuthResponse(accessToken, refreshToken);
    }

    /**
     * 소셜 가입자 비밀번호 설정 (일반 로그인 전환)
     */
    @Transactional
    public void setPassword(String email, SetPasswordRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        if (member.getPassword() != null) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_PASSWORD_ALREADY_EXISTS);
        }

        member.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    /**
     * 소셜 연동 해제
     */
    @Transactional
    public void unlinkSocial(String email, UnlinkSocialRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        // 연동 내역 확인
        if (!socialAccountRepository.existsByMemberAndProvider(member, request.provider())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_SOCIAL_NOT_FOUND);
        }

        // 최소 하나의 로그인 수단이 남는지 확인 (일반 로그인 가능 여부 또는 다른 소셜 연동)
        List<SocialAccount> socialAccounts = socialAccountRepository.findAllByMember(member);
        boolean hasPassword = member.getPassword() != null;

        if (!hasPassword && socialAccounts.size() <= 1) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_SOCIAL_UNLINK_FORBIDDEN);
        }

        socialAccountRepository.deleteByMemberAndProvider(member, request.provider());
    }
    
    /**
     * 로그아웃 V2
     */
    @CacheEvict(value = "refreshTokens", key = "#email")
    public void logoutV2(String email, String accessToken) {
        // 블랙리스트 전용 캐시 조회
        Cache blacklistCache = cacheManager.getCache("blacklistedTokens");
        if (blacklistCache != null) {
            blacklistCache.put(accessToken, "logout");
        }
    }

    /**
     * 토큰 재발급 V2
     */
    @Transactional
    public TokenAuthResponse reissueV2(String email, String refreshToken) {
        // 캐시에서 리프레시 토큰 조회
        Cache cache = getRefreshCache();

        // 저장소 내 존재 확인
        String cachedToken = cache.get(email, String.class);

        if (cachedToken == null || !cachedToken.equals(refreshToken)) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_TOKEN);
        }

        // 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_EXPIRED_TOKEN);
        }

        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        // 새 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(
                member.getEmail(), member.getRole().name());
        String newRefreshToken = jwtTokenProvider.createRefreshToken();

        // 캐시 업데이트
        cache.put(member.getEmail(), newRefreshToken);

        return new TokenAuthResponse(newAccessToken, newRefreshToken);
    }

    /**
     * 멤버 상태 처리 로직
     */
    private void handleMemberStatus(Member member) {
        switch (member.getStatus()) {
            case WITHDRAWN -> throw new ServiceException(ErrorEnum.ERR_AUTH_WITHDRAWN_MEMBER);
            case INACTIVE_SUSPENDED -> throw new ServiceException(
                    ErrorEnum.ERR_AUTH_SUSPENDED_MEMBER, member.getStatusReason());
            case INACTIVE_DORMANT -> member.activate();
            case ACTIVE -> {}
        }
    }

    /**
     * Refresh Token 전용 캐시 저장소를 가져오는 로직
     */
    private Cache getRefreshCache() {
        return Objects.requireNonNull(cacheManager.getCache("refreshTokens"), "CacheConfig에 refreshTokens 캐시 설정이 누락");
    }
}
