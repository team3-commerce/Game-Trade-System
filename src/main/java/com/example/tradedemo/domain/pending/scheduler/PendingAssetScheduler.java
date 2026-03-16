package com.example.tradedemo.domain.pending.scheduler;

import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.repository.PendingAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingAssetScheduler {

    private final PendingAssetRepository pendingAssetRepository;

    /**
     * 1분마다 실행
     * 만료된 수령 대기 상태 변경
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expirePendingAssets() {

        LocalDateTime now = LocalDateTime.now();

        List<PendingAsset> expiredAssets =
                pendingAssetRepository.findByExpiredAtBeforeAndIsClaimedFalse(now);

        if (expiredAssets.isEmpty()) {
            log.info("[PendingAsset Scheduler] 만료 대상 없음");
            return;
        }

        for (PendingAsset asset : expiredAssets) {
            asset.setExpireType();
        }

        log.info("[PendingAsset Scheduler] 만료 처리 완료 count={}", expiredAssets.size());
    }
}
