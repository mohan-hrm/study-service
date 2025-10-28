package com.preclinical.study.repository;

import com.preclinical.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {
    Optional<Study> findByStudyCode(String studyCode);
}
