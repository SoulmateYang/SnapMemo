package com.aibrief.scheduler;

import com.aibrief.service.BriefingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BriefingScheduler {

    private static final Logger log = LoggerFactory.getLogger(BriefingScheduler.class);
    private final BriefingService briefingService;

    public BriefingScheduler(BriefingService briefingService) {
        this.briefingService = briefingService;
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Shanghai")
    public void generateDailyBriefing() {
        log.info("Scheduler triggered: generating daily briefing");
        try {
            briefingService.generate();
        } catch (Exception e) {
            log.error("Scheduler failed to generate briefing: {}", e.getMessage());
        }
    }
}
