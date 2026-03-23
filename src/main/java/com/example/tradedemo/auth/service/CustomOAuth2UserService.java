package com.example.tradedemo.auth.service;

import com.example.tradedemo.auth.dto.*;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.domain.coupon.service.CouponService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.SocialAccount;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.enums.MemberStatus;
import com.example.tradedemo.domain.members.enums.SocialProvider;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.members.repository.SocialAccountRepository;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;
import java.math.BigDecimal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final WalletRepository walletRepository;
    private final CouponService couponService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialProvider provider = SocialProvider.from(registrationId);

        log.info("Social login attempt - provider: {}, registrationId: {}", provider, registrationId);
        
        if (provider == null) {
            throw new OAuth2AuthenticationException(ErrorEnum.ERR_AUTH_SOCIAL_UNSUPPORTED_PROVIDER.getErrorMessage());
        }

        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfo userInfo = getOAuth2UserInfo(provider, attributes);

        String email = userInfo.getEmail();
        if (email == null || email.trim().isEmpty()) {
            email = userInfo.getId() + "@" + provider.name().toLowerCase() + ".com";
            log.info("Social login email is empty, generated virtual email: {}", email);
        }

        Member member = processOAuth2User(provider, userInfo, email);

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        return new PrincipalDetails(member, attributes, userNameAttributeName);
    }

    private OAuth2UserInfo getOAuth2UserInfo(SocialProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            case GITHUB -> new GithubOAuth2UserInfo(attributes);
        };
    }

    private Member processOAuth2User(SocialProvider provider, OAuth2UserInfo userInfo, String email) {
        // 소셜 계정 연동 여부 확인
        Member member = socialAccountRepository.findByProviderAndProviderId(provider, userInfo.getId())
                .map(SocialAccount::getMember)
                .orElseGet(() -> {
                    // 소셜 계정은 없으나 동일한 이메일의 기존 회원이 있는지 확인 (계정 통합)
                    Member existingMember = memberRepository.findByEmail(email)
                            .orElseGet(() -> registerNewMember(userInfo, email));
                    
                    // 소셜 계정 연결
                    socialAccountRepository.save(SocialAccount.create(existingMember, provider, userInfo.getId()));
                    return existingMember;
                });

        // 계정 상태 체크 (활동 중이 아닌 경우 로그인을 차단)
        if (member.getStatus() != MemberStatus.ACTIVE) {
            log.warn("Blocked login attempt for {} member: {}", member.getStatus(), email);
            throw new OAuth2AuthenticationException(ErrorEnum.ERR_AUTH_NOT_ACTIVE_STATUS.getErrorMessage());
        }

        return member;
    }

    private Member registerNewMember(OAuth2UserInfo userInfo, String email) {
        String nickname = userInfo.getNickname();
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = "User_" + userInfo.getId().substring(0, Math.min(6, userInfo.getId().length()));
        }
        
        if (memberRepository.existsByNickname(nickname)) {
            nickname = nickname + "_" + userInfo.getId().substring(0, Math.min(4, userInfo.getId().length()));
        }

        Member member = Member.createSocial(email, nickname, MemberRole.USER);
        memberRepository.save(member);

        // 지갑 생성
        walletRepository.save(Wallet.create(member, BigDecimal.ZERO));

        // 회원가입 쿠폰 자동 발급
        couponService.autoSignupCoupon(member);

        return member;
    }
}
