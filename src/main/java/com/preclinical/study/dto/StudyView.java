package com.preclinical.study.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyView implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long studyId;
    private String studyCode;
    private String studyTitle;
    private String studyType;
    private String sponsorName;
    private String protocolNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer version;
    private List<StudyPhaseView> phases;

}
