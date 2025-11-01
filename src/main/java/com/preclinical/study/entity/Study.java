package com.preclinical.study.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.preclinical.study.validation.UniqueProtocolNumber;
import com.preclinical.study.validation.ValidationGroups;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

@NamedEntityGraph(
	    name = "Study.withPhases",
	    attributeNodes = @NamedAttributeNode("phases")
	)
@Data
@Entity
@Table(name = "study")
public class Study implements Serializable{
	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long studyId;

   // @NotBlank(message = "Study code is required")
    @Size(max = 50)
    @Column(name = "study_code", nullable = false, unique = true)
    private String studyCode;

    //@NotBlank(message = "Study title cannot be blank")
    @Size(max = 255)
    @Column(name = "study_title", nullable = false)
    private String studyTitle;

    //@NotBlank(message = "Study type is required")
    //@Pattern(regexp = "Clinical|Preclinical", message = "Study type must be Clinical or Preclinical")
    @Column(name = "study_type", length = 50)
    private String studyType;

    @Size(max = 150)
    @Column(name = "sponsor_name", length = 150)
    private String sponsorName;

    //@NotBlank(message = "Protocol number is required")
    @Size(max = 100)
    @UniqueProtocolNumber (groups = ValidationGroups.OnCreate.class)
    @Column(name = "protocol_number", unique = true, length = 100)
    private String protocolNumber;

    //@NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status", length = 30)
    private String status = "Planned";

    //@NotBlank(message = "Created by is required")
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @JsonProperty("version")
    @Version // 🔹 Enables optimistic locking
    @Column(name = "version", nullable = false)
    private Integer version;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<StudyPhase> phases;

}
