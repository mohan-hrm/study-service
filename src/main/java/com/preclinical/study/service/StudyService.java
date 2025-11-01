package com.preclinical.study.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.preclinical.study.dto.CreateStudyRequest;
import com.preclinical.study.dto.StudyView;
import com.preclinical.study.dto.UpdateStudyRequest;
import com.preclinical.study.entity.Study;
import com.preclinical.study.event.StudyCreatedEvent;
import com.preclinical.study.mapper.StudyMapper;
import com.preclinical.study.repository.StudyRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;

@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final StudyMapper studyMapper;


    public StudyService(StudyRepository studyRepository, ApplicationEventPublisher eventPublisher, StudyMapper studyMapper) {
        this.studyRepository = studyRepository;
        this.eventPublisher = eventPublisher;
        this.studyMapper = studyMapper;
    }
    
    @Cacheable(value = "#{@cacheConfigProperties.names['studyListCache']}")
    public List<StudyView> findAll() {
        return studyMapper.toViewList(studyRepository.findAll());
    }

    @Cacheable(value = "#{@cacheConfigProperties.names['studyCache']}", key = "#code")
    @Transactional(readOnly = true)
    public StudyView findByCode(String code) {
    	return studyRepository.findByStudyCode(code)
                .map(studyMapper ::toView)
                .orElseThrow(() -> new EntityNotFoundException("Study not found with ID: " + code));
    }

    @Caching(evict = {
    	    @CacheEvict(value = "#{@cacheConfigProperties.names['studyListCache']}", allEntries = true),
    	    @CacheEvict(value = "#{@cacheConfigProperties.names['studyStatusCache']}", allEntries = true),
    	    @CacheEvict(value = "#{@cacheConfigProperties.names['studyDateRangeCache']}", allEntries = true)
    	})
    @Transactional
    public StudyView createStudy(CreateStudyRequest request) {
        Study study = studyMapper.fromCreateRequest(request);
        Study saved = studyRepository.save(study);

        eventPublisher.publishEvent(new StudyCreatedEvent(this, saved));

        if (study.getStudyCode().startsWith("FAIL")) {
            throw new RuntimeException("Simulated DB failure — rolling back transaction");
        }

        return studyMapper.toView(saved);
    }
    
    @Cacheable(
    	    value = "#{@cacheConfigProperties.names['studyStatusCache']}",
    	    key = "#status + '-' + #pageable.pageNumber + '-' + #pageable.pageSize"
    	)
    public Page<StudyView> getStudiesByStatus(String status, Pageable pageable) {
        return studyRepository.findByStatus(status, pageable).map(studyMapper::toView);
    }
    
    @Cacheable(
    	    value = "#{@cacheConfigProperties.names['studyDateRangeCache']}",
    	    key = "#start.toString() + '-' + #end.toString()"
    	)
    public List<StudyView> getStudiesBetweenDates(LocalDate start, LocalDate end) {
        return studyMapper.toViewList(studyRepository.findStudiesWithinDates(start, end));
    }

    @Caching(
    		put = { @CachePut(value = "#{@cacheConfigProperties.names['studyCache']}", key = "#id") },
    		evict = {
    	    @CacheEvict(value = "#{@cacheConfigProperties.names['studyCache']}", key = "#id"),
    	    @CacheEvict(value = "#{@cacheConfigProperties.names['studyListCache']}", allEntries = true),
    	    @CacheEvict(value = "#{@cacheConfigProperties.names['studyStatusCache']}", allEntries = true),
    	    @CacheEvict(value = "#{@cacheConfigProperties.names['studyDateRangeCache']}", allEntries = true)
    	})
    @Transactional
    public void markStudyCompleted(Long studyId) {
        studyRepository.updateStudyStatus(studyId, "Completed");
    }
    
    /**
     * Fetch a study by ID (throws EntityNotFoundException if missing)
     */
    @Cacheable(value = "#{@cacheConfigProperties.names['studyCache']}", key = "#id")
    @Transactional(readOnly = true)
    public StudyView getStudyById(Long id) {
        return studyRepository.findById(id)
                .map(studyMapper::toView)
                .orElseThrow(() -> new EntityNotFoundException("Study not found with ID: " + id));
    }
    
    /**
     * Update study safely with optimistic locking.
     * 
     * @param id      Study ID
     * @param request Study details sent from client, includes version
     * @return Updated study
     */
    @Transactional
    @Caching(
    		put = { @CachePut(value = "#{@cacheConfigProperties.names['studyCache']}", key = "#id") },
    		evict = {
    	    @CacheEvict(value = "#{@cacheConfigProperties.names['studyCache']}", key = "#id"),
    	    @CacheEvict(value = "#{@cacheConfigProperties.names['studyListCache']}", allEntries = true),
    	    @CacheEvict(value = "#{@cacheConfigProperties.names['studyStatusCache']}", allEntries = true),
    	    @CacheEvict(value = "#{@cacheConfigProperties.names['studyDateRangeCache']}", allEntries = true),
    	    // 💡 NOW: clear the code-based entry using the return value
            @CacheEvict(value = "#{@cacheConfigProperties.names['studyCache']}", key = "#result.studyCode")
    	})
    public StudyView updateStudy(Long id, UpdateStudyRequest request) {

    	Study existing = studyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Study not found"));

        if (!Objects.equals(existing.getVersion(), request.getVersion())) {
            throw new OptimisticLockException("Study was modified by another user");
        }

        // ✅ Merge DTO into managed entity
        studyMapper.updateEntityFromDto(request, existing);
        existing.setUpdatedAt(LocalDateTime.now());

        Study saved = studyRepository.save(existing);
        return studyMapper.toView(saved);
    }
}
