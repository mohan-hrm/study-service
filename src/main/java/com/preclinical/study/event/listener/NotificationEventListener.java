package com.preclinical.study.event.listener;

import com.preclinical.study.event.StudyCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    @Async("mailExecutor")
    @EventListener
    public void handleStudyCreated(StudyCreatedEvent event) {
        var study = event.getStudy();

        log.info("[NOTIFY] Sending email for Study {} to sponsor {} (Thread: {})",
                study.getStudyCode(), study.getSponsorName(),
                Thread.currentThread().getName());

        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        log.info("[NOTIFY] Email sent successfully for Study {}", study.getStudyCode());
    }
}

