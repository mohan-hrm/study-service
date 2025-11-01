package com.preclinical.study.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyPhaseView implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long phaseId;
    private String phaseName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
