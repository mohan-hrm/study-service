package com.preclinical.study.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudyRequest {

    @NotBlank(message = "Study code is required")
    @Size(max = 50)
    private String studyCode;

    @NotBlank(message = "Study title is required")
    @Size(max = 255)
    private String studyTitle;

    @NotBlank(message = "Study type is required")
    @Pattern(regexp = "Clinical|Preclinical", message = "Study type must be Clinical or Preclinical")
    private String studyType;

    @Size(max = 150)
    private String sponsorName;

    @NotBlank(message = "Protocol number is required")
    @Size(max = 100)
    private String protocolNumber;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank(message = "Created by is required")
    private String createdBy;
}
