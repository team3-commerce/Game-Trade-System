package com.example.tradedemo.domain.members.scheduler;

import com.example.tradedemo.common.consts.MemberPolicy;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberStatus;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberScheduler {

    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void manageMemberLifecycle() {
        log.info("회원 생명주기 관리 스케줄러");

        processDormantMember();
        processDeleteMember();

        log.info("회원 생명주기 관리 스케줄러 종료");
    }

    private void processDormantMember() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(MemberPolicy.DORMANT_THRESHOLD_DAYS);

        List<Member> targets = memberRepository.findAllByStatusAndLastLoginAtBefore(MemberStatus.ACTIVE, threshold);

        for (Member member : targets) {
            member.makeDormant();
        }
        log.info("휴면 전환 처리 완료: {}건", targets.size());
    }

    private void processDeleteMember() {
        LocalDateTime threshold = LocalDateTime.now().minusYears(MemberPolicy.WITHDRAWAL_RETENTION_YEARS);

        List<Member> targets =
                memberRepository.findAllByStatusAndStatusChangedAtBefore(MemberStatus.WITHDRAWN, threshold);

        memberRepository.deleteAll(targets);
        log.info("탈퇴 데이터 삭제 완료: {}건", targets.size());
    }
}
