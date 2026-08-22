package com.example.tradedemo.domain.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public abstract class BaseExpireJob implements Job {

    /**
     * 실제 만료 처리 로직을 서브 클래스에서 구현
     */
    @Transactional
    protected abstract void executeExpireLogic();

    @Override
    public void execute(JobExecutionContext context) {
        try {
            executeExpireLogic();
        } catch (Exception e) {
            log.error("[Quartz] Job 실행 중 오류 발생", e);
        }
    }
}