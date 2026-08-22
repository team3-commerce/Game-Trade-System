package com.example.tradedemo.domain.members.service;

import static com.example.tradedemo.domain.members.consts.MemberConst.V3_MEMBER_CACHE_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.dto.GetMyInfoResponse;
import com.example.tradedemo.domain.members.dto.SuspendMemberRequest;
import com.example.tradedemo.domain.members.dto.UpdateNicknameRequest;
import com.example.tradedemo.domain.members.dto.UpdatePasswordRequest;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.enums.MemberStatus;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.pending.repository.PendingAssetRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MarketListingRepository marketListingRepository;

    @Mock
    private PendingAssetRepository pendingAssetRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Test
    @DisplayName("내 정보 조회 V1 성공")
    void getMyInfo_v1_success() {
        // given
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        // when
        GetMyInfoResponse response = memberService.getMyInfo(email);

        // then
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.nickname()).isEqualTo("nickname");
    }

    @Test
    @DisplayName("내 정보 조회 V2 성공")
    void getMyInfo_v2_success() {
        // given
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        // when
        GetMyInfoResponse response = memberService.getMyInfoV2(email);

        // then
        assertThat(response.email()).isEqualTo(email);
        verify(memberRepository).findByEmail(email);
    }

    @Test
    @DisplayName("내 정보 조회 V3 성공 - Redis 캐시 적중")
    void getMyInfo_v3_success_cache_hit() {
        // given
        String email = "test@example.com";
        String cacheKey = V3_MEMBER_CACHE_PREFIX + email;
        GetMyInfoResponse cachedResponse = new GetMyInfoResponse(email, "nickname", MemberRole.USER);

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(cacheKey)).willReturn(cachedResponse);

        // when
        GetMyInfoResponse response = memberService.getMyInfoV3(email);

        // then
        assertThat(response.email()).isEqualTo(email);
        verify(memberRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("내 정보 조회 V3 성공 - Redis 캐시 미적중 시 DB 조회 및 캐시 저장")
    void getMyInfo_v3_success_cache_miss() {
        // given
        String email = "test@example.com";
        String cacheKey = V3_MEMBER_CACHE_PREFIX + email;
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(cacheKey)).willReturn(null);
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));

        // when
        GetMyInfoResponse response = memberService.getMyInfoV3(email);

        // then
        assertThat(response.email()).isEqualTo(email);
        verify(valueOperations).set(eq(cacheKey), any(), any());
    }

    @Test
    @DisplayName("닉네임 수정 V1 성공")
    void updateNickname_v1_success() {
        // given
        String email = "test@example.com";
        UpdateNicknameRequest request = new UpdateNicknameRequest("newNickname");
        Member member = Member.create(email, "password", "oldNickname", MemberRole.USER);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(memberRepository.existsByNickname(anyString())).willReturn(false);

        // when
        memberService.updateNickname(email, request);

        // then
        assertThat(member.getNickname()).isEqualTo("newNickname");
    }

    @Test
    @DisplayName("닉네임 수정 V2 성공")
    void updateNickname_v2_success() {
        // given
        String email = "test@example.com";
        UpdateNicknameRequest request = new UpdateNicknameRequest("newNicknameV2");
        Member member = Member.create(email, "password", "oldNickname", MemberRole.USER);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(memberRepository.existsByNickname(anyString())).willReturn(false);

        // when
        memberService.updateNicknameV2(email, request);

        // then
        assertThat(member.getNickname()).isEqualTo("newNicknameV2");
    }

    @Test
    @DisplayName("닉네임 수정 V3 성공 - Redis 캐시 삭제 확인")
    void updateNickname_v3_success() {
        // given
        String email = "test@example.com";
        UpdateNicknameRequest request = new UpdateNicknameRequest("newNicknameV3");
        Member member = Member.create(email, "password", "oldNickname", MemberRole.USER);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(memberRepository.existsByNickname(anyString())).willReturn(false);

        // when
        memberService.updateNicknameV3(email, request);

        // then
        assertThat(member.getNickname()).isEqualTo("newNicknameV3");
        verify(redisTemplate).delete(V3_MEMBER_CACHE_PREFIX + email);
    }

    @Test
    @DisplayName("닉네임 수정 실패 - 중복된 닉네임")
    void updateNickname_fail_duplicate() {
        // given
        String email = "test@example.com";
        UpdateNicknameRequest request = new UpdateNicknameRequest("duplicateNickname");
        Member member = Member.create(email, "password", "oldNickname", MemberRole.USER);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(memberRepository.existsByNickname(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.updateNickname(email, request))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    @DisplayName("비밀번호 수정 V1 성공")
    void updatePassword_v1_success() {
        // given
        String email = "test@example.com";
        UpdatePasswordRequest request = new UpdatePasswordRequest("currentPassword", "newPassword");
        Member member = Member.create(email, "encodedCurrentPassword", "nickname", MemberRole.USER);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(passwordEncoder.encode(anyString())).willReturn("encodedNewPassword");

        // when
        memberService.updatePassword(email, request);

        // then
        assertThat(member.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    @DisplayName("비밀번호 수정 V2 성공")
    void updatePassword_v2_success() {
        // given
        String email = "test@example.com";
        UpdatePasswordRequest request = new UpdatePasswordRequest("currentPassword", "newPasswordV2");
        Member member = Member.create(email, "encodedCurrentPassword", "nickname", MemberRole.USER);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(passwordEncoder.encode(anyString())).willReturn("encodedNewPasswordV2");

        // when
        memberService.updatePasswordV2(email, request);

        // then
        assertThat(member.getPassword()).isEqualTo("encodedNewPasswordV2");
    }

    @Test
    @DisplayName("비밀번호 수정 V3 성공 - Redis 캐시 삭제 확인")
    void updatePassword_v3_success() {
        // given
        String email = "test@example.com";
        UpdatePasswordRequest request = new UpdatePasswordRequest("currentPassword", "newPasswordV3");
        Member member = Member.create(email, "encodedCurrentPassword", "nickname", MemberRole.USER);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(passwordEncoder.encode(anyString())).willReturn("encodedNewPassword");

        // when
        memberService.updatePasswordV3(email, request);

        // then
        assertThat(member.getPassword()).isEqualTo("encodedNewPassword");
        verify(redisTemplate).delete(V3_MEMBER_CACHE_PREFIX + email);
    }

    @Test
    @DisplayName("비밀번호 수정 실패 - 잘못된 현재 비밀번호")
    void updatePassword_fail_invalid_current() {
        // given
        String email = "test@example.com";
        UpdatePasswordRequest request = new UpdatePasswordRequest("wrongPassword", "newPassword");
        Member member = Member.create(email, "encodedCurrentPassword", "nickname", MemberRole.USER);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.updatePassword(email, request))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    @DisplayName("회원 탈퇴 V1 성공")
    void withdraw_v1_success() {
        // given
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);
        ReflectionTestUtils.setField(member, "id", 1L);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(marketListingRepository.existsByMemberIdAndStatus(1L, MarketListingStatus.SELLING)).willReturn(false);
        given(pendingAssetRepository.existsByMemberIdAndIsClaimedFalse(1L)).willReturn(false);

        // when
        memberService.withdraw(email);

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
        assertThat(member.getRefreshToken()).isNull();
    }

    @Test
    @DisplayName("회원 탈퇴 V2 성공")
    void withdraw_v2_success() {
        // given
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);
        ReflectionTestUtils.setField(member, "id", 1L);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(marketListingRepository.existsByMemberIdAndStatus(1L, MarketListingStatus.SELLING)).willReturn(false);
        given(pendingAssetRepository.existsByMemberIdAndIsClaimedFalse(1L)).willReturn(false);

        // when
        memberService.withdrawV2(email);

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
    }

    @Test
    @DisplayName("회원 탈퇴 V3 성공 - Redis 캐시 삭제 확인")
    void withdraw_v3_success() {
        // given
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);
        ReflectionTestUtils.setField(member, "id", 1L);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(marketListingRepository.existsByMemberIdAndStatus(1L, MarketListingStatus.SELLING)).willReturn(false);
        given(pendingAssetRepository.existsByMemberIdAndIsClaimedFalse(1L)).willReturn(false);

        // when
        memberService.withdrawV3(email);

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.WITHDRAWN);
        verify(redisTemplate).delete(V3_MEMBER_CACHE_PREFIX + email);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 판매 중인 상품 존재")
    void withdraw_fail_active_listings() {
        // given
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);
        ReflectionTestUtils.setField(member, "id", 1L);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(marketListingRepository.existsByMemberIdAndStatus(1L, MarketListingStatus.SELLING)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.withdraw(email))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 수령 대기 자산 존재")
    void withdraw_fail_pending_assets() {
        // given
        String email = "test@example.com";
        Member member = Member.create(email, "password", "nickname", MemberRole.USER);
        ReflectionTestUtils.setField(member, "id", 1L);

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(marketListingRepository.existsByMemberIdAndStatus(1L, MarketListingStatus.SELLING)).willReturn(false);
        given(pendingAssetRepository.existsByMemberIdAndIsClaimedFalse(1L)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.withdraw(email))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    @DisplayName("회원 정지 V1 성공")
    void suspendMember_v1_success() {
        // given
        SuspendMemberRequest request = new SuspendMemberRequest("target@example.com", "정지 사유");
        Member member = Member.create(request.email(), "password", "nickname", MemberRole.USER);

        given(memberRepository.findByEmail(request.email())).willReturn(Optional.of(member));

        // when
        memberService.suspendMember(request);

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.INACTIVE_SUSPENDED);
        assertThat(member.getStatusReason()).isEqualTo("정지 사유");
    }

    @Test
    @DisplayName("회원 정지 V2 성공")
    void suspendMember_v2_success() {
        // given
        SuspendMemberRequest request = new SuspendMemberRequest("target@example.com", "정지 사유 V2");
        Member member = Member.create(request.email(), "password", "nickname", MemberRole.USER);

        given(memberRepository.findByEmail(request.email())).willReturn(Optional.of(member));

        // when
        memberService.suspendMemberV2(request);

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.INACTIVE_SUSPENDED);
    }

    @Test
    @DisplayName("회원 정지 V3 성공 - Redis 캐시 삭제 확인")
    void suspendMember_v3_success() {
        // given
        SuspendMemberRequest request = new SuspendMemberRequest("target@example.com", "정지 사유");
        Member member = Member.create(request.email(), "password", "nickname", MemberRole.USER);

        given(memberRepository.findByEmail(request.email())).willReturn(Optional.of(member));

        // when
        memberService.suspendMemberV3(request);

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.INACTIVE_SUSPENDED);
        verify(redisTemplate).delete(V3_MEMBER_CACHE_PREFIX + request.email());
    }

    @Test
    @DisplayName("회원 정지 실패 - 이미 탈퇴한 회원")
    void suspendMember_fail_withdrawn() {
        // given
        SuspendMemberRequest request = new SuspendMemberRequest("withdrawn@example.com", "정지 사유");
        Member member = Member.create(request.email(), "password", "nickname", MemberRole.USER);
        member.withdraw();

        given(memberRepository.findByEmail(request.email())).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> memberService.suspendMember(request))
                .isInstanceOf(ServiceException.class);
    }
}
