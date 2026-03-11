package com.example.tradedemo.domain.members.repository;

import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByNickname(String nickname);

    // 30일 이상 미접속한 ACTIVE 상태 유저 조회
    List<Member> findAllByStatusAndLastLoginAtBefore(MemberStatus status, LocalDateTime dateTime);

    // 탈퇴한 지 1년이 지난 유저 조회
    List<Member> findAllByStatusAndStatusChangedAtBefore(MemberStatus status, LocalDateTime dateTime);
}
