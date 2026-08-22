package com.example.tradedemo.auth.service;

import static com.example.tradedemo.auth.consts.AuthConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.tradedemo.auth.dto.*;
import com.example.tradedemo.auth.provider.JwtTokenProvider;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.coupon.service.CouponService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.SocialAccount;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.enums.MemberStatus;
import com.example.tradedemo.domain.members.enums.SocialProvider;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.members.repository.SocialAccountRepository;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SocialAccountRepository socialAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CouponService couponService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private Cache cache;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        SignupAuthRequest request = new SignupAuthRequest("test@example.com", "password", "nickname", MemberRole.USER);
        given(memberRepository.findByEmail(request.email())).willReturn(Optional.empty());
        given(memberRepository.existsByNickname(request.nickname())).willReturn(false);
        given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");

        // when
        authService.signup(request);

        // then
        verify(memberRepository).save(any(Member.class));
        verify(walletRepository).save(any());
        verify(couponService).autoSignupCoupon(any(Member.class));
    }

    @Test
    @DisplayName("로그인 V1 성공")
    void login_success() {
        // given
        LoginAuthRequest request = new LoginAuthRequest("test@example.com", "password");
        Member member = Member.create("test@example.com", "encodedPassword", "nickname", MemberRole.USER);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(anyString(), anyString())).willReturn("accessToken");
        given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("refreshToken");

        // when
        TokenAuthResponse response = authService.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("accessToken");
        assertThat(response.refreshToken()).isEqualTo("refreshToken");
        assertThat(member.getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("로그인 V2 성공 - 캐시 저장 확인")
    void loginV2_success() {
        // given
        LoginAuthRequest request = new LoginAuthRequest("test@example.com", "password");
        Member member = Member.create("test@example.com", "encodedPassword", "nickname", MemberRole.USER);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(anyString(), anyString())).willReturn("accessToken");
        given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("refreshToken");
        given(cacheManager.getCache("refreshTokens")).willReturn(cache);

        // when
        TokenAuthResponse response = authService.loginV2(request);

        // then
        verify(cache).put(eq("test@example.com"), eq("refreshToken"));
        assertThat(response.refreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("로그인 V3 성공 - Redis 저장 확인")
    void loginV3_success() {
        // given
        LoginAuthRequest request = new LoginAuthRequest("test@example.com", "password");
        Member member = Member.create("test@example.com", "encodedPassword", "nickname", MemberRole.USER);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(anyString(), anyString())).willReturn("accessToken");
        given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("refreshToken");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        authService.loginV3(request);

        // then
        verify(valueOperations)
                .set(eq(V3_REFRESH_TOKEN_PREFIX + "test@example.com"), eq("refreshToken"), eq(V3_REFRESH_TOKEN_TTL));
    }

    @Test
    @DisplayName("토큰 재발급 V1 성공 - DB 확인")
    void reissue_success() {
        // given
        String refreshToken = "validRefreshToken";
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);
        member.updateRefreshToken(refreshToken);

        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getUserEmail(refreshToken)).willReturn(email);
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(jwtTokenProvider.createAccessToken(anyString(), anyString())).willReturn("newAccessToken");
        given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("newRefreshToken");

        // when
        TokenAuthResponse response = authService.reissue(refreshToken);

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

        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getUserEmail(refreshToken)).willReturn(email);
        given(cacheManager.getCache("refreshTokens")).willReturn(cache);
        given(cache.get(email, String.class)).willReturn(refreshToken);
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(jwtTokenProvider.createAccessToken(anyString(), anyString())).willReturn("newAccessToken");
        given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("newRefreshToken");

        // when
        TokenAuthResponse response = authService.reissueV2(refreshToken);

        // then
        assertThat(response.accessToken()).isEqualTo("newAccessToken");
        verify(cache).put(email, "newRefreshToken");
    }

    @Test
    @DisplayName("토큰 재발급 V3 성공 - Redis 확인")
    void reissueV3_success() {
        // given
        String refreshToken = "validRefreshToken";
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);

        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getUserEmail(refreshToken)).willReturn(email);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(V3_REFRESH_TOKEN_PREFIX + email)).willReturn(refreshToken);
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(jwtTokenProvider.createAccessToken(anyString(), anyString())).willReturn("newAccessToken");
        given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("newRefreshToken");

        // when
        TokenAuthResponse response = authService.reissueV3(refreshToken);

        // then
        assertThat(response.accessToken()).isEqualTo("newAccessToken");
        verify(valueOperations).set(eq(V3_REFRESH_TOKEN_PREFIX + email), eq("newRefreshToken"), eq(V3_REFRESH_TOKEN_TTL));
    }

    @Test
    @DisplayName("로그아웃 V2 성공 - 블랙리스트 등록")
    void logoutV2_success() {
        // given
        String email = "test@example.com";
        String accessToken = "accessToken";
        given(cacheManager.getCache(BLACKLIST_CACHE_NAME)).willReturn(cache);

        // when
        authService.logoutV2(email, accessToken);

        // then
        verify(cache).put(accessToken, "logout");
    }

    @Test
    @DisplayName("로그아웃 V3 성공 - Redis 블랙리스트 등록")
    void logoutV3_success() {
        // given
        String email = "test@example.com";
        String accessToken = "accessToken";
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        authService.logoutV3(email, accessToken);

        // then
        verify(redisTemplate).delete(V3_REFRESH_TOKEN_PREFIX + email);
        verify(valueOperations)
                .set(eq(V3_BLACKLIST_TOKEN_PREFIX + accessToken), eq("logout"), eq(V3_BLACKLIST_TOKEN_TTL));
    }

    @Test
    @DisplayName("비밀번호 설정 V2 성공")
    void setPassword_success() {
        // given
        String email = "test@example.com";
        SetPasswordRequest request = new SetPasswordRequest("newPassword");
        Member member = Member.createSocial(email, "nickname", MemberRole.USER);
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(passwordEncoder.encode("newPassword")).willReturn("encodedNewPassword");

        // when
        authService.setPassword(email, request);

        // then
        assertThat(member.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    @DisplayName("비밀번호 설정 V3 성공 - Redis 리프레시 토큰 무효화")
    void setPasswordV3_success() {
        // given
        String email = "test@example.com";
        SetPasswordRequest request = new SetPasswordRequest("newPassword");
        Member member = Member.createSocial(email, "nickname", MemberRole.USER);
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(passwordEncoder.encode("newPassword")).willReturn("encodedNewPassword");

        // when
        authService.setPasswordV3(email, request);

        // then
        verify(redisTemplate).delete(V3_REFRESH_TOKEN_PREFIX + email);
    }

    @Test
    @DisplayName("소셜 연동 해제 성공")
    void unlinkSocial_success() {
        // given
        String email = "test@example.com";
        UnlinkSocialRequest request = new UnlinkSocialRequest(SocialProvider.KAKAO);
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(socialAccountRepository.existsByMemberAndProvider(member, SocialProvider.KAKAO))
                .willReturn(true);

        // when
        authService.unlinkSocial(email, request);

        // then
        verify(socialAccountRepository).deleteByMemberAndProvider(member, SocialProvider.KAKAO);
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

        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(socialAccountRepository.existsByMemberAndProvider(member, SocialProvider.KAKAO))
                .willReturn(true);
        given(socialAccountRepository.findAllByMember(member)).willReturn(List.of(socialAccount));

        // when & then
        assertThatThrownBy(() -> authService.unlinkSocial(email, request)).isInstanceOf(ServiceException.class);
    }

    @Test
    @DisplayName("회원 상태 처리 - 휴면 계정 로그인 시 활성화")
    void login_dormant_member_activates() {
        // given
        LoginAuthRequest request = new LoginAuthRequest("test@example.com", "password");
        Member member = Member.create("test@example.com", "encodedPassword", "nickname", MemberRole.USER);
        member.makeDormant();

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(anyString(), anyString())).willReturn("at");
        given(jwtTokenProvider.createRefreshToken(anyString())).willReturn("rt");

        // when
        authService.login(request);

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }
}
