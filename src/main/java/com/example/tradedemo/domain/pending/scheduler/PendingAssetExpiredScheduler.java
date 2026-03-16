package com.example.tradedemo.domain.pending.scheduler;

import com.example.tradedemo.domain.pending.entity.PendingAsset;
import com.example.tradedemo.domain.pending.entity.PendingAssetExpiredHistory;
import com.example.tradedemo.domain.pending.repository.PendingAssetExpiredHistoryRepository;
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
public class PendingAssetExpiredScheduler {

    private final PendingAssetRepository pendingAssetRepository;
    private final PendingAssetExpiredHistoryRepository historyRepository;


    /**
     * 1분마다 실행
     * 만료된 수령 대기 자산 삭제
     * 수령대기기록에 남김
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expirePendingAssets() {

        LocalDateTime now = LocalDateTime.now();

        List<PendingAsset> expiredAssets =
                pendingAssetRepository.findByExpiredAtBeforeAndIsClaimedFalse(now);

        if (expiredAssets.isEmpty()) {
            log.info("[PendingAsset 만료] 없음");
            return;
        }

        List<PendingAssetExpiredHistory> histories =
                expiredAssets.stream()
                        .map(PendingAssetExpiredHistory::create)
                        .toList();

        historyRepository.saveAll(histories);

        pendingAssetRepository.deleteAll(expiredAssets);

        log.info("[수령대기(PendingAsset) 만료 처리 완료] 삭제 건수={}", expiredAssets.size());
    }
}
