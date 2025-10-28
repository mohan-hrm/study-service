package com.preclinical.study.event.listener;

import com.preclinical.study.event.StudyCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AuditEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);

    @Async("eventExecutor")
    @EventListener
    public void handleStudyCreated(StudyCreatedEvent event) {
        var study = event.getStudy();
        log.info("[AUDIT] Study created: {} | Title: {} | By: {} | Thread: {}",
                study.getStudyCode(), study.getStudyTitle(),
                study.getCreatedBy(), Thread.currentThread().getName());

        // Simulate audit DB insert or logging
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        log.info("[AUDIT] Completed audit for Study {}", study.getStudyCode());
    }
}
