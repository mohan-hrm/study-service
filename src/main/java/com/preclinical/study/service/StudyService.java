package com.preclinical.study.service;

import com.preclinical.study.entity.Study;
import com.preclinical.study.event.StudyCreatedEvent;
import com.preclinical.study.repository.StudyRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final ApplicationEventPublisher eventPublisher;


    public StudyService(StudyRepository studyRepository, ApplicationEventPublisher eventPublisher) {
        this.studyRepository = studyRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<Study> findAll() {
        return studyRepository.findAll();
    }

    public Optional<Study> findByCode(String code) {
        return studyRepository.findByStudyCode(code);
    }

    public Study createStudy(Study study) {
        // populate audit fields if needed
        study.setCreatedAt(LocalDateTime.now());
        study.setUpdatedAt(LocalDateTime.now());

        Study saved = studyRepository.save(study);

        // publish domain event
        eventPublisher.publishEvent(new StudyCreatedEvent(this, saved));

        return saved;
    }
}
