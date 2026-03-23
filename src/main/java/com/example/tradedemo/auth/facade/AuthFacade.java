package com.example.tradedemo.auth.facade;

import com.example.tradedemo.auth.dto.*;
import com.example.tradedemo.auth.service.AuthService;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.service.CouponService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.SocialAccount;
import com.example.tradedemo.domain.members.enums.SocialProvider;
import com.example.tradedemo.domain.members.service.MemberService;
import com.example.tradedemo.domain.members.service.SocialAccountService;
import com.example.tradedemo.domain.wallet.service.WalletService;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthService authService;
    private final MemberService memberService;
    private final WalletService walletService;
    private final CouponService couponService;
    private final SocialAccountService socialAccountService;

    /**
     * 회원가입
     */
    @Transactional
    public void signup(SignupAuthRequest request) {
        // 비밀번호 암호화
        String encodedPassword = authService.encodePassword(request.password());

        // 멤버 생성
        Member member = memberService.createMember(request.email(), encodedPassword, request.nickname(), request.role());

        // 지갑 생성
        walletService.createWallet(member, BigDecimal.ZERO);

        // 쿠폰 발급
        couponService.autoSignupCoupon(member);
    }

    /**
     * 로그인 V1
     */
    @Transactional
    public TokenAuthResponse login(LoginAuthRequest request) {
        Member member = memberService.findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        authService.validatePassword(request.password(), member.getPassword());
        
        memberService.handleMemberStatus(member); // 계정 상태 확인

        TokenAuthResponse response = authService.createTokens(member.getEmail(), member.getRole().name());

        member.updateRefreshToken(response.refreshToken());
        member.updateLastLoginAt();

        return response;
    }

    /**
     * 로그인 V2 (Caffeine Cache)
     */
    @Transactional
    public TokenAuthResponse loginV2(LoginAuthRequest request) {
        Member member = memberService.findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        if (member.getPassword() == null) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_SOCIAL_ACCOUNT_ONLY);
        }

        authService.validatePassword(request.password(), member.getPassword());
        
        memberService.handleMemberStatus(member);

        TokenAuthResponse response = authService.createTokens(member.getEmail(), member.getRole().name());

        authService.saveRefreshTokenToCache(member.getEmail(), response.refreshToken());
        member.updateLastLoginAt();

        return response;
    }

    /**
     * 로그인 V3 (Redis)
     */
    @Transactional
    public TokenAuthResponse loginV3(LoginAuthRequest request) {
        Member member = memberService.findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        if (member.getPassword() == null) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_SOCIAL_ACCOUNT_ONLY);
        }

        authService.validatePassword(request.password(), member.getPassword());
        
        memberService.handleMemberStatus(member);

        TokenAuthResponse response = authService.createTokens(member.getEmail(), member.getRole().name());

        authService.saveRefreshTokenToRedis(member.getEmail(), response.refreshToken());
        member.updateLastLoginAt();

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
     * 로그아웃 V2
     */
    public void logoutV2(String email, String accessToken) {
        authService.addToBlacklistCache(accessToken);
        // 리프레시 토큰 캐시는 @CacheEvict로 Controller 수준에서 처리하거나 여기서 명시적으로 삭제
    }

    /**
     * 로그아웃 V3
     */
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

        if (member.getRefreshToken() == null || !member.getRefreshToken().equals(refreshToken)) {
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
        String cachedToken = authService.getRefreshTokenFromCache(email);

        if (cachedToken == null || !cachedToken.equals(refreshToken)) {
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
        String cachedToken = authService.getRefreshTokenFromRedis(email);

        if (cachedToken == null || !cachedToken.equals(refreshToken)) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_TOKEN);
        }

        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        TokenAuthResponse response = authService.createTokens(member.getEmail(), member.getRole().name());
        authService.saveRefreshTokenToRedis(email, response.refreshToken());

        return response;
    }

    /**
     * 비밀번호 설정
     */
    @Transactional
    public void setPassword(String email, SetPasswordRequest request) {
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        if (member.getPassword() != null) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_PASSWORD_ALREADY_EXISTS);
        }

        member.updatePassword(authService.encodePassword(request.newPassword()));
    }

    /**
     * 소셜 연동 해제
     */
    @Transactional
    public void unlinkSocial(String email, UnlinkSocialRequest request) {
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        if (!socialAccountService.existsByMemberAndProvider(member, request.provider())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_SOCIAL_NOT_FOUND);
        }

        List<SocialAccount> socialAccounts = socialAccountService.findAllByMember(member);
        boolean hasPassword = member.getPassword() != null;

        if (!hasPassword && socialAccounts.size() <= 1) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_SOCIAL_UNLINK_FORBIDDEN);
        }

        socialAccountService.deleteByMemberAndProvider(member, request.provider());
    }
}
