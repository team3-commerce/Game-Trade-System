package com.example.tradedemo.domain.members.repository;

import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.SocialAccount;
import com.example.tradedemo.domain.members.enums.SocialProvider;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    
    @Query("select sa from SocialAccount sa join fetch sa.member where sa.provider = :provider and sa.providerId = :providerId")
    Optional<SocialAccount> findByProviderAndProviderId(@Param("provider") SocialProvider provider, @Param("providerId") String providerId);
    
    List<SocialAccount> findAllByMember(Member member);
    boolean existsByMemberAndProvider(Member member, SocialProvider provider);
    void deleteByMemberAndProvider(Member member, SocialProvider provider);
}
