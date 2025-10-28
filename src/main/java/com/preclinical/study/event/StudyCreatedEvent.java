package com.preclinical.study.event;

import com.preclinical.study.entity.Study;
import org.springframework.context.ApplicationEvent;

/**
 * Custom domain event fired when a new Study is created.
 */
public class StudyCreatedEvent extends ApplicationEvent {

    private final Study study;

    public StudyCreatedEvent(Object source, Study study) {
        super(source);
        this.study = study;
    }

    public Study getStudy() {
        return study;
    }
}

