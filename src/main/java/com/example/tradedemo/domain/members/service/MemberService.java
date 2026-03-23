package com.example.tradedemo.domain.members.service;

import static com.example.tradedemo.auth.consts.AuthConst.REFRESH_TOKEN_CACHE_NAME;
import static com.example.tradedemo.domain.members.consts.MemberConst.*;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.members.dto.GetMyInfoResponse;
import com.example.tradedemo.domain.members.dto.SuspendMemberRequest;
import com.example.tradedemo.domain.members.dto.UpdateNicknameRequest;
import com.example.tradedemo.domain.members.dto.UpdatePasswordRequest;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.enums.MemberStatus;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 회원 생성
     */
    @Transactional
    public Member createMember(String email, String password, String nickname, MemberRole role) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_DUPLICATE_EMAIL);
        }
        if (memberRepository.existsByNickname(nickname)) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_DUPLICATE_NICKNAME);
        }
        Member member = Member.create(email, password, nickname, role);
        return memberRepository.save(member);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /**
     * 멤버 상태 처리 로직
     */
    @Transactional
    public void handleMemberStatus(Member member) {
        switch (member.getStatus()) {
            case WITHDRAWN -> throw new ServiceException(ErrorEnum.ERR_AUTH_WITHDRAWN_MEMBER);
            case INACTIVE_SUSPENDED -> throw new ServiceException(
                    ErrorEnum.ERR_AUTH_SUSPENDED_MEMBER, member.getStatusReason());
            case INACTIVE_DORMANT -> member.activate();
            case ACTIVE -> {}
        }
    }

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
        return getMyInfo(email);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public GetMyInfoResponse getMyInfoV3(String email) {
        String cacheKey = V3_MEMBER_CACHE_PREFIX + email;
        Object cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            if (cached instanceof GetMyInfoResponse response) return response;
            if (cached instanceof Map map) {
                try {
                    return objectMapper.convertValue(map, GetMyInfoResponse.class);
                } catch (IllegalArgumentException e) {
                    log.error("캐시 변환 실패 - Key: {}, Error: {}", cacheKey, e.getMessage());
                }
            }
        }

        GetMyInfoResponse response = getMyInfo(email);
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

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_DUPLICATE_NICKNAME);
        }
        member.updateNickname(request.nickname());
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
        @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email")
    })
    public void updateNicknameV2(String email, UpdateNicknameRequest request) {
        updateNickname(email, request);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
        @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email")
    })
    public void updateNicknameV3(String email, UpdateNicknameRequest request) {
        updateNickname(email, request);
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

        if (!passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
            throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_PASSWORD);
        }
        member.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
        @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email")
    })
    public void updatePasswordV2(String email, UpdatePasswordRequest request) {
        updatePassword(email, request);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
        @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email")
    })
    public void updatePasswordV3(String email, UpdatePasswordRequest request) {
        updatePassword(email, request);
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
        member.withdraw();
        member.clearRefreshToken();
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
        @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email"),
        @CacheEvict(value = REFRESH_TOKEN_CACHE_NAME, key = "#email")
    })
    public void withdrawV2(String email) {
        withdraw(email);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#email"),
        @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#email"),
        @CacheEvict(value = REFRESH_TOKEN_CACHE_NAME, key = "#email")
    })
    public void withdrawV3(String email) {
        withdraw(email);
        redisTemplate.delete(V3_MEMBER_CACHE_PREFIX + email);
        // Auth 관련 Redis 삭제는 Auth 도메인에서 처리하는 것이 좋으나, 현재는 하위 호환성을 위해 유지하거나 Facade로 이관 고려
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
    @Caching(evict = {
        @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#request.email()"),
        @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#request.email()")
    })
    public void suspendMemberV2(SuspendMemberRequest request) {
        suspendMember(request);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = MEMBERS_CACHE_NAME, key = "#request.email()"),
        @CacheEvict(value = MEMBER_AUTHS_CACHE_NAME, key = "#request.email()")
    })
    public void suspendMemberV3(SuspendMemberRequest request) {
        suspendMember(request);
        redisTemplate.delete(V3_MEMBER_CACHE_PREFIX + request.email());
    }

    @Transactional(readOnly = true)
    public Member findMember(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
    }
}
