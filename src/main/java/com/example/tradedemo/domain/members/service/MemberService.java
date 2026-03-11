package com.example.tradedemo.domain.members.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.members.dto.MemberResponse;
import com.example.tradedemo.domain.members.dto.NicknameUpdateRequest;
import com.example.tradedemo.domain.members.dto.PasswordUpdateRequest;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 내 정보 조회
     */
    public MemberResponse getMyInfo(String email) {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }

    /**
     * 내 닉네임 수정
     */
    @Transactional
    public void updateNickname(String email, NicknameUpdateRequest request) {
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
    public void updatePassword(String email, PasswordUpdateRequest request) {
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
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
        memberRepository.delete(member);
    }
}
