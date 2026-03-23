package com.example.tradedemo.domain.members.service;

import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.SocialAccount;
import com.example.tradedemo.domain.members.enums.SocialProvider;
import com.example.tradedemo.domain.members.repository.SocialAccountRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialAccountService {

    private final SocialAccountRepository socialAccountRepository;

    public boolean existsByMemberAndProvider(Member member, SocialProvider provider) {
        return socialAccountRepository.existsByMemberAndProvider(member, provider);
    }

    public List<SocialAccount> findAllByMember(Member member) {
        return socialAccountRepository.findAllByMember(member);
    }

    @Transactional
    public void deleteByMemberAndProvider(Member member, SocialProvider provider) {
        socialAccountRepository.deleteByMemberAndProvider(member, provider);
    }
}
