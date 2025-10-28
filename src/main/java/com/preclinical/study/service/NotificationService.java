package com.preclinical.study.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Async("mailExecutor")
    public void sendEmail(String studyCode, String userEmail) {
        log.info("Start sending email for Study {} to {} (Thread: {})",
                 studyCode, userEmail, Thread.currentThread().getName());
        try {
            Thread.sleep(3000); // simulate SMTP delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Email sent for Study {} (Thread: {})",
                 studyCode, Thread.currentThread().getName());
    }
}
