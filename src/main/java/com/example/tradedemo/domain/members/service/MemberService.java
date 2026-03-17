package com.example.tradedemo.domain.members.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.dto.GetMyInfoResponse;
import com.example.tradedemo.domain.members.dto.SuspendMemberRequest;
import com.example.tradedemo.domain.members.dto.UpdateNicknameRequest;
import com.example.tradedemo.domain.members.dto.UpdatePasswordRequest;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.enums.MemberStatus;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.pending.repository.PendingAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MarketListingRepository marketListingRepository;
    private final PendingAssetRepository pendingAssetRepository;

    /**
     * 내 정보 조회
     */
    public GetMyInfoResponse getMyInfo(String email) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
        return GetMyInfoResponse.from(member);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Cacheable(value = "members", key = "#email")
    public GetMyInfoResponse getMyInfoV2(String email) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
        return GetMyInfoResponse.from(member);
    }

    /**
     * 내 닉네임 수정
     */
    @Transactional
    public void updateNickname(String email, UpdateNicknameRequest request) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_DUPLICATE_NICKNAME);
        }

        // 새 닉네임 업데이트
        member.updateNickname(request.nickname());
    }

    @Transactional
    @Caching(evict = {@CacheEvict(value = "members", key = "#email"), @CacheEvict(value = "memberAuths", key = "#email")})
    public void updateNicknameV2(String email, UpdateNicknameRequest request) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_DUPLICATE_NICKNAME);
        }

        // 새 닉네임 업데이트
        member.updateNickname(request.nickname());
    }

    /**
     * 내 비밀번호 수정
     */
    @Transactional
    public void updatePassword(String email, UpdatePasswordRequest request) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        // 현재 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_PASSWORD);
        }

        // 새 비밀번호 업데이트
        member.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    @Caching(evict = {@CacheEvict(value = "members", key = "#email"), @CacheEvict(value = "memberAuths", key = "#email")})
    public void updatePasswordV2(String email, UpdatePasswordRequest request) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        // 현재 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_PASSWORD);
        }

        // 새 비밀번호 업데이트
        member.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    /**
     * 회원탈퇴
     */
    @Transactional
    public void withdraw(String email) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        // 거래소에 판매 중인 상품이 있는지 확인
        if (marketListingRepository.existsByMemberIdAndStatus(member.getId(), MarketListingStatus.SELLING)) {
            throw new ServiceException(ErrorEnum.ERR_MEMBER_HAS_ACTIVE_LISTINGS);
        }

        // 수령 대기 중인 자산이 있는지 확인
        if (pendingAssetRepository.existsByMemberIdAndIsClaimedFalse(member.getId())) {
            throw new ServiceException(ErrorEnum.ERR_MEMBER_HAS_PENDING_ASSETS);
        }

        member.withdraw();
        member.clearRefreshToken();
    }

    @Transactional
    @Caching(evict = {@CacheEvict(value = "members", key = "#email"), @CacheEvict(value = "memberAuths", key = "#email")})
    public void withdrawV2(String email) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        // 거래소에 판매 중인 상품이 있는지 확인
        if (marketListingRepository.existsByMemberIdAndStatus(member.getId(), MarketListingStatus.SELLING)) {
            throw new ServiceException(ErrorEnum.ERR_MEMBER_HAS_ACTIVE_LISTINGS);
        }

        // 수령 대기 중인 자산이 있는지 확인
        if (pendingAssetRepository.existsByMemberIdAndIsClaimedFalse(member.getId())) {
            throw new ServiceException(ErrorEnum.ERR_MEMBER_HAS_PENDING_ASSETS);
        }

        member.withdraw();
        member.clearRefreshToken();
    }

    /**
     * 회원 정지(관리자)
     */
    @Transactional
    public void suspendMember(SuspendMemberRequest request) {
        Member member = memberRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_WITHDRAWN_MEMBER);
        }

        member.suspend(request.reason());
    }

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = "members", key = "#request.email()"),
                @CacheEvict(value = "memberAuths", key = "#request.email()")
            })
    public void suspendMemberV2(SuspendMemberRequest request) {
        Member member = memberRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_WITHDRAWN_MEMBER);
        }

        member.suspend(request.reason());
    }

    @Transactional(readOnly = true)
    public Member findMember(Long memberId) {

        return memberRepository.findById(memberId).orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
    }
}
