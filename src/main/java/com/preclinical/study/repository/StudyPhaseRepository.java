package com.preclinical.study.repository;

import com.preclinical.study.entity.StudyPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudyPhaseRepository extends JpaRepository<StudyPhase, Long> {
    List<StudyPhase> findByStudy_StudyId(Long studyId);
}
