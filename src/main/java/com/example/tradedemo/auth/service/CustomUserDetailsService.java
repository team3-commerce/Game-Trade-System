package com.example.tradedemo.auth.service;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return memberRepository.findByEmail(email)
                .map(PrincipalDetails::new)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
    }
}
