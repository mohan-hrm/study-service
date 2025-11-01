package com.preclinical.study.event;

import com.preclinical.study.entity.Study;
import org.springframework.context.ApplicationEvent;

/**
 * Custom domain event fired when a new Study is created.
 */
public class StudyCreatedEvent extends ApplicationEvent {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1871060907768482903L;
	private final Study study;

    public StudyCreatedEvent(Object source, Study study) {
        super(source);
        this.study = study;
    }

    public Study getStudy() {
        return study;
    }
}

