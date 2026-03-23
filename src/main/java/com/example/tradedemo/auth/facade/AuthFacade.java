package com.example.tradedemo.auth.facade;

import com.example.tradedemo.auth.dto.*;
import com.example.tradedemo.auth.service.AuthService;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.service.CouponService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.service.MemberService;
import com.example.tradedemo.domain.wallet.facade.WalletFacade;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthService authService;
    private final MemberService memberService;
    private final WalletFacade walletFacade;
    private final CouponService couponService;

    /**
     * 회원 가입 및 지갑 생성 (V1)
     */
    @Transactional
    public void signup(SignupAuthRequest request) {
        String encodedPassword = authService.encodePassword(request.password());
        Member member = memberService.createMember(
                request.email(),
                encodedPassword,
                request.nickname(),
                request.role()
        );

        // 초기 잔액 0원인 지갑 생성
        walletFacade.createWallet(member, BigDecimal.ZERO);

        // 회원가입 쿠폰 자동 발급
        couponService.autoSignupCoupon(member);
    }

    /**
     * 로그인 V1 (DB 기반)
     */
    @Transactional
    public TokenAuthResponse login(LoginAuthRequest request) {
        Member member = memberService.findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        authService.validatePassword(request.password(), member.getPassword());
        memberService.handleMemberStatus(member);

        TokenAuthResponse response = authService.createTokens(member.getEmail(), member.getRole().name());
        member.updateRefreshToken(response.refreshToken());

        return response;
    }

    /**
     * 로그인 V2 (캐시 기반)
     */
    @Transactional
    public TokenAuthResponse loginV2(LoginAuthRequest request) {
        Member member = memberService.findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        // 일반 로그인 시 소셜 계정 체크
        if (member.isSocialOnly()) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_SOCIAL_ACCOUNT_ONLY);
        }

        authService.validatePassword(request.password(), member.getPassword());
        memberService.handleMemberStatus(member);

        TokenAuthResponse response = authService.createTokens(member.getEmail(), member.getRole().name());
        authService.saveRefreshTokenToCache(member.getEmail(), response.refreshToken());

        return response;
    }

    /**
     * 로그인 V3 (Redis 기반)
     */
    @Transactional
    public TokenAuthResponse loginV3(LoginAuthRequest request) {
        Member member = memberService.findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        if (member.isSocialOnly()) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_SOCIAL_ACCOUNT_ONLY);
        }

        authService.validatePassword(request.password(), member.getPassword());
        memberService.handleMemberStatus(member);

        TokenAuthResponse response = authService.createTokens(member.getEmail(), member.getRole().name());
        authService.saveRefreshTokenToRedis(member.getEmail(), response.refreshToken());

        return response;
    }

    /**
     * 로그아웃 V1
     */
    @Transactional
    public void logout(String email) {
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));
        member.clearRefreshToken();
    }

    /**
     * 로그아웃 V2 (캐시 블랙리스트)
     */
    @Transactional
    public void logoutV2(String email, String accessToken) {
        // 캐시 기반 로그아웃 (생략 가능하나 구조상 유지)
        authService.addToBlacklistCache(accessToken);
    }

    /**
     * 로그아웃 V3 (Redis 블랙리스트)
     */
    @Transactional
    public void logoutV3(String email, String accessToken) {
        authService.deleteRefreshTokenFromRedis(email);
        authService.addToBlacklistRedis(accessToken);
    }

    /**
     * 토큰 재발급 V1
     */
    @Transactional
    public TokenAuthResponse reissue(String refreshToken) {
        String email = authService.validateAndGetEmail(refreshToken);
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        if (!refreshToken.equals(member.getRefreshToken())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_TOKEN);
        }

        TokenAuthResponse response = authService.createTokens(member.getEmail(), member.getRole().name());
        member.updateRefreshToken(response.refreshToken());

        return response;
    }

    /**
     * 토큰 재발급 V2
     */
    @Transactional
    public TokenAuthResponse reissueV2(String refreshToken) {
        String email = authService.validateAndGetEmail(refreshToken);
        String savedToken = authService.getRefreshTokenFromCache(email);

        if (!refreshToken.equals(savedToken)) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_TOKEN);
        }

        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        TokenAuthResponse response = authService.createTokens(member.getEmail(), member.getRole().name());
        authService.saveRefreshTokenToCache(email, response.refreshToken());

        return response;
    }

    /**
     * 토큰 재발급 V3
     */
    @Transactional
    public TokenAuthResponse reissueV3(String refreshToken) {
        String email = authService.validateAndGetEmail(refreshToken);
        String savedToken = authService.getRefreshTokenFromRedis(email);

        if (!refreshToken.equals(savedToken)) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_TOKEN);
        }

        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        TokenAuthResponse response = authService.createTokens(member.getEmail(), member.getRole().name());
        authService.saveRefreshTokenToRedis(email, response.refreshToken());

        return response;
    }

    /**
     * 소셜 가입자 비밀번호 설정
     */
    @Transactional
    public void setPassword(String email, SetPasswordRequest request) {
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        String encodedPassword = authService.encodePassword(request.newPassword());
        member.updatePassword(encodedPassword);
    }

    /**
     * 소셜 연동 해제
     */
    @Transactional
    public void unlinkSocial(String email, UnlinkSocialRequest request) {
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        member.unlinkSocial(request.provider());
    }

    /**
     * 소셜 연동 해제 V2 (캐시 기반)
     */
    @Transactional
    public void unlinkSocialV2(String email, UnlinkSocialRequest request) {
        unlinkSocial(email, request);
        authService.deleteRefreshTokenFromCache(email);
    }

    /**
     * 소셜 연동 해제 V3 (Redis 기반)
     */
    @Transactional
    public void unlinkSocialV3(String email, UnlinkSocialRequest request) {
        unlinkSocial(email, request);
        authService.deleteRefreshTokenFromRedis(email);
    }
}
