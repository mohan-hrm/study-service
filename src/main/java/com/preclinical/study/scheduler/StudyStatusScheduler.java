package com.preclinical.study.scheduler;

import com.preclinical.study.entity.Study;
import com.preclinical.study.lock.DbLockManager;
import com.preclinical.study.repository.StudyRepository;
import com.preclinical.study.service.AuditService;
import com.preclinical.study.service.NotificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StudyStatusScheduler {

    private static final Logger log = LoggerFactory.getLogger(StudyStatusScheduler.class);
    private final StudyRepository studyRepository;
    private final AuditService auditService;
    private final DbLockManager lockManager;
    
    @Autowired
    private NotificationService notificationService;


    // from application-local.yml
    @Value("${scheduler.study.activate-cron:0 0 1 * * *}")
    private String cronExp;

    public StudyStatusScheduler(StudyRepository studyRepository, AuditService auditService, DbLockManager lockManager) {
        this.studyRepository = studyRepository;
        this.auditService = auditService;
        this.lockManager = lockManager;
    }

    /**
     * Every day at 1 AM, activate Planned studies whose start_date <= today.
     * Uses DB lock to avoid multi-pod duplicate runs.
     */
    @Transactional
    @Scheduled(cron = "${scheduler.study.activate-cron}")
    public void activatePlannedStudies() {
        String podName = System.getenv().getOrDefault("HOSTNAME", "local");
        String lockName = "activate-studies-job";

        if (!lockManager.tryLock(lockName, podName)) {
            log.info("Another pod holds the lock, skipping execution.");
            return;
        }

        long start = System.currentTimeMillis();
        log.info("Pod {} acquired lock. Scheduler started...", podName);

        try {
            LocalDate today = LocalDate.now();
            List<Study> planned = studyRepository.findAll().stream()
                    .filter(s -> "Planned".equalsIgnoreCase(s.getStatus())
                            && s.getStartDate() != null
                            && !s.getStartDate().isAfter(today))
                    .toList();

            planned.forEach(s -> {
                s.setStatus("Ongoing");
                s.setUpdatedAt(LocalDateTime.now());
                studyRepository.save(s);
                auditService.logOnce("Activated", s.getStudyCode());
            });

            log.info("Scheduler completed: {} studies activated.", planned.size());
        } catch (Exception e) {
            log.error("Error in activatePlannedStudies job", e);
        } finally {
            lockManager.releaseLock(lockName, podName);
            long duration = System.currentTimeMillis() - start;
            log.info("Pod {} released lock. Duration: {} ms", podName, duration);
        }
    }

    /**
     * Heartbeat demo: logs every 30s (visible locally).
     */
    @Scheduled(fixedRate = 30000)
    public void demoHeartbeat() {
        log.info("Heartbeat Scheduler: study-service alive at {}", LocalDateTime.now());
    }
    
    @Scheduled(fixedRate = 60000)
    public void simulateAsyncNotification() {
        log.info("Triggering async email jobs...");
        notificationService.sendEmail("ST-1001", "alice@example.com");
        notificationService.sendEmail("ST-1002", "bob@example.com");
        log.info("Main thread continues without waiting.");
    }
}
