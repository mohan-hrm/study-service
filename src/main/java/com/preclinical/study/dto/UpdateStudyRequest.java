package com.preclinical.study.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudyRequest {

    @Size(max = 255)
    private String studyTitle;

    @Size(max = 150)
    private String sponsorName;

    @Size(max = 100)
    private String protocolNumber;

    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull(message = "Version is required for update")
    private Integer version;
}
