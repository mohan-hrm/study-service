package com.preclinical.study.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Simple Audit placeholder for demonstration.
 * In production, this would write to a study_audit table.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    public void logOnce(String actionType, String studyCode) {
        // TODO: Implement unique log tracking by studyCode + actionType
        log.info("Audit: {} action recorded for Study {}", actionType, studyCode);
    }
}
