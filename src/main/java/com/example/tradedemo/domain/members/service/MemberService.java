package com.example.tradedemo.domain.members.service;

import static com.example.tradedemo.auth.consts.AuthConst.REFRESH_TOKEN_CACHE_NAME;
import static com.example.tradedemo.auth.consts.AuthConst.V3_REFRESH_TOKEN_PREFIX;
import static com.example.tradedemo.domain.members.consts.MemberConst.*;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MarketListingRepository marketListingRepository;
    private final PendingAssetRepository pendingAssetRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

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
    @Cacheable(value = MEMBERS_CACHE_NAME, key = "#email")
    public GetMyInfoResponse getMyInfoV2(String email) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
        return GetMyInfoResponse.from(member);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetMyInfoResponse getMyInfoV3(String email) {
        String cacheKey = V3_MEMBER_CACHE_PREFIX + email;
        Object cached = redisTemplate.opsForValue().get(cacheKey);

        // 캐시 적중 시 처리 로직
        if (cached != null) {
            // 이미 올바른 타입인 경우
            if (cached instanceof GetMyInfoResponse response) {
                return response;
            }

            // LinkedHashMap으로 역직렬화된 경우 변환 시도
            if (cached instanceof Map map) {
                try {
                    return objectMapper.convertValue(map, GetMyInfoResponse.class);
                } catch (IllegalArgumentException e) {
                    log.error("캐시 변환 실패 - Key: {}, Error: {}", cacheKey, e.getMessage());
                }
            } else {
                log.warn(
                        "알 수 없는 캐시 데이터 타입 발견 - Key: {}, Type: {}",
                        cacheKey,
                        cached.getClass().getName());
            }
        }

        // 캐시 미스 또는 변환 실패 시 DB 조회
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        GetMyInfoResponse response = GetMyInfoResponse.from(member);

        // 캐시 최신화
        redisTemplate.opsForValue().set(cacheKey, response, V3_MEMBER_CACHE_TTL);

        return response;
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
    @Caching(
            evict = {
                @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
                @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email")
            })
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

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
                @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email")
            })
    public void updateNicknameV3(String email, UpdateNicknameRequest request) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_DUPLICATE_NICKNAME);
        }

        member.updateNickname(request.nickname());

        // V3 캐시 삭제
        redisTemplate.delete(V3_MEMBER_CACHE_PREFIX + email);
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
    @Caching(
            evict = {
                @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
                @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email")
            })
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

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
                @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email")
            })
    public void updatePasswordV3(String email, UpdatePasswordRequest request) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_PASSWORD);
        }

        member.updatePassword(passwordEncoder.encode(request.newPassword()));

        // V3 캐시 삭제
        redisTemplate.delete(V3_MEMBER_CACHE_PREFIX + email);
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
    @Caching(
            evict = {
                @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
                @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email"),
                @CacheEvict(value = REFRESH_TOKEN_CACHE_NAME, key = "#email")
            })
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

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
                @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email"),
                @CacheEvict(value = REFRESH_TOKEN_CACHE_NAME, key = "#email")
            })
    public void withdrawV3(String email) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        if (marketListingRepository.existsByMemberIdAndStatus(member.getId(), MarketListingStatus.SELLING)) {
            throw new ServiceException(ErrorEnum.ERR_MEMBER_HAS_ACTIVE_LISTINGS);
        }

        if (pendingAssetRepository.existsByMemberIdAndIsClaimedFalse(member.getId())) {
            throw new ServiceException(ErrorEnum.ERR_MEMBER_HAS_PENDING_ASSETS);
        }

        member.withdraw();
        member.clearRefreshToken();

        // V3 캐시 삭제
        redisTemplate.delete(V3_MEMBER_CACHE_PREFIX + email);
        redisTemplate.delete(V3_REFRESH_TOKEN_PREFIX + email);
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
                @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#request.email()"),
                @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#request.email()")
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

    @Transactional
    @Caching(
            evict = {
                @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#request.email()"),
                @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#request.email()")
            })
    public void suspendMemberV3(SuspendMemberRequest request) {
        Member member = memberRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND));

        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_WITHDRAWN_MEMBER);
        }

        member.suspend(request.reason());

        // 캐시 삭제
        redisTemplate.delete(V3_MEMBER_CACHE_PREFIX + request.email());
    }

    @Transactional(readOnly = true)
    public Member findMember(Long memberId) {

        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
    }
}
