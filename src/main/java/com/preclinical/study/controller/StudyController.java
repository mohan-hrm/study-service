package com.preclinical.study.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.preclinical.study.dto.CreateStudyRequest;
import com.preclinical.study.dto.StudyView;
import com.preclinical.study.dto.UpdateStudyRequest;
import com.preclinical.study.service.StudyService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/study")
public class StudyController {

    @Value("${sample.message:Default Message from Study-Service}")
    private String message;

    @Value("${study.username:unknown}")
    private String dbUser;
    
    private final StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    @GetMapping("/info")
    public String info() {
        return "Study-Service is running ✅\nMessage: " + message + "\nDB User: " + dbUser;
    }
    @GetMapping("/all")
    public ResponseEntity<List<StudyView>> getAll() {
        return ResponseEntity.ok(studyService.findAll());
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        try {
            StudyView study = studyService.findByCode(code);
            return ResponseEntity.ok(study);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Study not found with code: " + code));
        }
    }
    
    @GetMapping("/id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            StudyView study = studyService.getStudyById(id);
            return ResponseEntity.ok(study);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Study not found with Id: " + id));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createStudy(@Valid @RequestBody CreateStudyRequest request) {
        try {
            StudyView created = studyService.createStudy(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<StudyView>> getStudiesByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        List<String> allowedSortFields = List.of(
                "studyCode", "studyTitle", "startDate", "endDate",
                "status", "createdAt", "updatedAt"
        );

        String sortField = (sortBy != null && allowedSortFields.contains(sortBy))
                ? sortBy : "startDate";

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<StudyView> pageResult = studyService.getStudiesByStatus(status, pageable);
        return ResponseEntity.ok(pageResult);
    }
    
    @GetMapping("/between")
    public ResponseEntity<List<StudyView>> getStudiesBetweenDates(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return ResponseEntity.ok(studyService.getStudiesBetweenDates(start, end));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> markCompleted(@PathVariable Long id) {
        studyService.markStudyCompleted(id);
        return ResponseEntity.ok(Map.of("message", "Study " + id + " marked as completed"));
    }
    
    /**
     * Patch endpoint with version field in payload.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStudy(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudyRequest request) {
        try {
            StudyView updated = studyService.updateStudy(id, request);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Study not found"));
        } catch (OptimisticLockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Study was updated by another user. Please refresh and retry."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}