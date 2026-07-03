package com.folioframe.domain.portfolio.scheduler;

import com.folioframe.domain.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// 저장(확정)되지 않은 채 방치된 포트폴리오 초안을 주기적으로 정리한다. 사용자가 템플릿만 선택하고
// 저장·AI첨삭·게시 중 아무것도 하지 않은 채 편집 화면을 나가면, 유예 기간이 지난 뒤 이 스케줄러가
// 해당 포트폴리오를 삭제한다.
@Component
@RequiredArgsConstructor
public class PortfolioDraftCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(PortfolioDraftCleanupScheduler.class);

    private final PortfolioService portfolioService;

    @Value("${portfolio.draft-cleanup.grace-period-hours:72}")
    private long gracePeriodHours;

    @Scheduled(cron = "${portfolio.draft-cleanup.cron:0 0 * * * *}")
    public void cleanupAbandonedDrafts() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(gracePeriodHours);
        int deleted = portfolioService.cleanupAbandonedDrafts(cutoff);
        if (deleted > 0) {
            log.info("미확정 초안 포트폴리오 {}건 정리 완료 (기준 시각: {})", deleted, cutoff);
        }
    }
}
