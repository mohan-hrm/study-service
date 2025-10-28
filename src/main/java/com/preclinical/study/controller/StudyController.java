package com.preclinical.study.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.preclinical.study.entity.Study;
import com.preclinical.study.service.StudyService;


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
    public List<Study> getAll() {
        return studyService.findAll();
    }

    @GetMapping("/{code}")
    public Optional<Study> getByCode(@PathVariable String code) {
        return studyService.findByCode(code);
    }

    @PostMapping("/create")
    public Study createStudy(@RequestBody Study study) {
        return studyService.createStudy(study);
    }
}