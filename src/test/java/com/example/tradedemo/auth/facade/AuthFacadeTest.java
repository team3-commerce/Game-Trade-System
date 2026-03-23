package com.example.tradedemo.auth.facade;

import static com.example.tradedemo.auth.consts.AuthConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.tradedemo.auth.dto.*;
import com.example.tradedemo.auth.service.AuthService;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.service.CouponService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.SocialAccount;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.enums.MemberStatus;
import com.example.tradedemo.domain.members.enums.SocialProvider;
import com.example.tradedemo.domain.members.service.MemberService;
import com.example.tradedemo.domain.members.service.SocialAccountService;
import com.example.tradedemo.domain.wallet.service.WalletService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthFacadeTest {

    @InjectMocks
    private AuthFacade authFacade;

    @Mock
    private AuthService authService;

    @Mock
    private MemberService memberService;

    @Mock
    private WalletService walletService;

    @Mock
    private CouponService couponService;

    @Mock
    private SocialAccountService socialAccountService;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        SignupAuthRequest request = new SignupAuthRequest("test@example.com", "password", "nickname", MemberRole.USER);
        Member member = Member.create(request.email(), "encodedPassword", request.nickname(), request.role());
        
        given(authService.encodePassword(request.password())).willReturn("encodedPassword");
        given(memberService.createMember(anyString(), anyString(), anyString(), any(MemberRole.class))).willReturn(member);

        // when
        authFacade.signup(request);

        // then
        verify(memberService).createMember(eq(request.email()), eq("encodedPassword"), eq(request.nickname()), eq(request.role()));
        verify(walletService).createWallet(any(Member.class), any(BigDecimal.class));
        verify(couponService).autoSignupCoupon(any(Member.class));
    }

    @Test
    @DisplayName("로그인 V1 성공")
    void login_success() {
        // given
        LoginAuthRequest request = new LoginAuthRequest("test@example.com", "password");
        Member member = Member.create("test@example.com", "encodedPassword", "nickname", MemberRole.USER);
        TokenAuthResponse tokenResponse = new TokenAuthResponse("accessToken", "refreshToken");

        given(memberService.findByEmail(anyString())).willReturn(Optional.of(member));
        given(authService.createTokens(anyString(), anyString())).willReturn(tokenResponse);

        // when
        TokenAuthResponse response = authFacade.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("accessToken");
        assertThat(response.refreshToken()).isEqualTo("refreshToken");
        assertThat(member.getRefreshToken()).isEqualTo("refreshToken");
        verify(authService).validatePassword(eq("password"), eq("encodedPassword"));
        verify(memberService).handleMemberStatus(member);
    }

    @Test
    @DisplayName("로그인 V2 성공 - 캐시 저장 확인")
    void loginV2_success() {
        // given
        LoginAuthRequest request = new LoginAuthRequest("test@example.com", "password");
        Member member = Member.create("test@example.com", "encodedPassword", "nickname", MemberRole.USER);
        TokenAuthResponse tokenResponse = new TokenAuthResponse("accessToken", "refreshToken");

        given(memberService.findByEmail(anyString())).willReturn(Optional.of(member));
        given(authService.createTokens(anyString(), anyString())).willReturn(tokenResponse);

        // when
        TokenAuthResponse response = authFacade.loginV2(request);

        // then
        verify(authService).saveRefreshTokenToCache(eq("test@example.com"), eq("refreshToken"));
        assertThat(response.refreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("로그인 V3 성공 - Redis 저장 확인")
    void loginV3_success() {
        // given
        LoginAuthRequest request = new LoginAuthRequest("test@example.com", "password");
        Member member = Member.create("test@example.com", "encodedPassword", "nickname", MemberRole.USER);
        TokenAuthResponse tokenResponse = new TokenAuthResponse("accessToken", "refreshToken");

        given(memberService.findByEmail(anyString())).willReturn(Optional.of(member));
        given(authService.createTokens(anyString(), anyString())).willReturn(tokenResponse);

        // when
        authFacade.loginV3(request);

        // then
        verify(authService).saveRefreshTokenToRedis(eq("test@example.com"), eq("refreshToken"));
    }

    @Test
    @DisplayName("토큰 재발급 V1 성공 - DB 확인")
    void reissue_success() {
        // given
        String refreshToken = "validRefreshToken";
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);
        member.updateRefreshToken(refreshToken);
        TokenAuthResponse tokenResponse = new TokenAuthResponse("newAccessToken", "newRefreshToken");

        given(authService.validateAndGetEmail(refreshToken)).willReturn(email);
        given(memberService.findByEmail(email)).willReturn(Optional.of(member));
        given(authService.createTokens(anyString(), anyString())).willReturn(tokenResponse);

        // when
        TokenAuthResponse response = authFacade.reissue(refreshToken);

        // then
        assertThat(response.accessToken()).isEqualTo("newAccessToken");
        assertThat(member.getRefreshToken()).isEqualTo("newRefreshToken");
    }

    @Test
    @DisplayName("토큰 재발급 V2 성공 - 캐시 확인")
    void reissueV2_success() {
        // given
        String refreshToken = "validRefreshToken";
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);
        TokenAuthResponse tokenResponse = new TokenAuthResponse("newAccessToken", "newRefreshToken");

        given(authService.validateAndGetEmail(refreshToken)).willReturn(email);
        given(authService.getRefreshTokenFromCache(email)).willReturn(refreshToken);
        given(memberService.findByEmail(email)).willReturn(Optional.of(member));
        given(authService.createTokens(anyString(), anyString())).willReturn(tokenResponse);

        // when
        TokenAuthResponse response = authFacade.reissueV2(refreshToken);

        // then
        assertThat(response.accessToken()).isEqualTo("newAccessToken");
        verify(authService).saveRefreshTokenToCache(email, "newRefreshToken");
    }

    @Test
    @DisplayName("토큰 재발급 V3 성공 - Redis 확인")
    void reissueV3_success() {
        // given
        String refreshToken = "validRefreshToken";
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);
        TokenAuthResponse tokenResponse = new TokenAuthResponse("newAccessToken", "newRefreshToken");

        given(authService.validateAndGetEmail(refreshToken)).willReturn(email);
        given(authService.getRefreshTokenFromRedis(email)).willReturn(refreshToken);
        given(memberService.findByEmail(email)).willReturn(Optional.of(member));
        given(authService.createTokens(anyString(), anyString())).willReturn(tokenResponse);

        // when
        TokenAuthResponse response = authFacade.reissueV3(refreshToken);

        // then
        assertThat(response.accessToken()).isEqualTo("newAccessToken");
        verify(authService).saveRefreshTokenToRedis(email, "newRefreshToken");
    }

    @Test
    @DisplayName("로그아웃 V2 성공 - 블랙리스트 등록")
    void logoutV2_success() {
        // given
        String email = "test@example.com";
        String accessToken = "accessToken";

        // when
        authFacade.logoutV2(email, accessToken);

        // then
        verify(authService).addToBlacklistCache(accessToken);
    }

    @Test
    @DisplayName("로그아웃 V3 성공 - Redis 블랙리스트 등록")
    void logoutV3_success() {
        // given
        String email = "test@example.com";
        String accessToken = "accessToken";

        // when
        authFacade.logoutV3(email, accessToken);

        // then
        verify(authService).deleteRefreshTokenFromRedis(email);
        verify(authService).addToBlacklistRedis(accessToken);
    }

    @Test
    @DisplayName("비밀번호 설정 성공")
    void setPassword_success() {
        // given
        String email = "test@example.com";
        SetPasswordRequest request = new SetPasswordRequest("newPassword");
        Member member = Member.createSocial(email, "nickname", MemberRole.USER);
        given(memberService.findByEmail(email)).willReturn(Optional.of(member));
        given(authService.encodePassword("newPassword")).willReturn("encodedNewPassword");

        // when
        authFacade.setPassword(email, request);

        // then
        assertThat(member.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    @DisplayName("소셜 연동 해제 성공")
    void unlinkSocial_success() {
        // given
        String email = "test@example.com";
        UnlinkSocialRequest request = new UnlinkSocialRequest(SocialProvider.KAKAO);
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);

        given(memberService.findByEmail(email)).willReturn(Optional.of(member));
        given(socialAccountService.existsByMemberAndProvider(member, SocialProvider.KAKAO))
                .willReturn(true);

        // when
        authFacade.unlinkSocial(email, request);

        // then
        verify(socialAccountService).deleteByMemberAndProvider(member, SocialProvider.KAKAO);
    }

    @Test
    @DisplayName("소셜 연동 해제 실패 - 유일한 로그인 수단인 경우")
    void unlinkSocial_fail_forbidden() {
        // given
        String email = "test@example.com";
        UnlinkSocialRequest request = new UnlinkSocialRequest(SocialProvider.KAKAO);
        // 비밀번호가 없고 소셜 계정이 하나뿐인 경우
        Member member = Member.create(email, null, "nickname", MemberRole.USER);
        SocialAccount socialAccount = SocialAccount.create(member, SocialProvider.KAKAO, "12345");

        given(memberService.findByEmail(email)).willReturn(Optional.of(member));
        given(socialAccountService.existsByMemberAndProvider(member, SocialProvider.KAKAO))
                .willReturn(true);
        given(socialAccountService.findAllByMember(member)).willReturn(List.of(socialAccount));

        // when & then
        assertThatThrownBy(() -> authFacade.unlinkSocial(email, request)).isInstanceOf(ServiceException.class);
    }
    
    private <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}
