package com.example.tradedemo.auth.service;

import com.example.tradedemo.auth.dto.LoginRequest;
import com.example.tradedemo.auth.dto.SignupRequest;
import com.example.tradedemo.auth.dto.TokenResponse;
import com.example.tradedemo.auth.provider.JwtTokenProvider;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.service.CouponService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CouponService couponService;
    private final WalletRepository walletRepository;

    /**
     * 회원가입
     */
    @Transactional
    public void signup(SignupRequest request) {
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
    public TokenResponse login(LoginRequest request) {
        // 사용자 확인
        Member member = memberRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_PASSWORD);
        }

        // 계정 상태별 처리
        switch (member.getStatus()) {
            case WITHDRAWN -> throw new ServiceException(ErrorEnum.ERR_AUTH_WITHDRAWN_MEMBER);

            case INACTIVE_SUSPENDED -> throw new ServiceException(
                    ErrorEnum.ERR_AUTH_SUSPENDED_MEMBER, member.getStatusReason());

            case INACTIVE_DORMANT -> member.activate();

            case ACTIVE -> {}
        }

        // 로그인 처리
        String accessToken = jwtTokenProvider.createAccessToken(
                member.getEmail(), member.getRole().name());

        String refreshToken = jwtTokenProvider.createRefreshToken();

        member.updateRefreshToken(refreshToken);
        member.updateLastLoginAt();

        return new TokenResponse(accessToken, refreshToken);
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
}
