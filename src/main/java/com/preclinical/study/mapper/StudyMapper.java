package com.preclinical.study.mapper;


import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.preclinical.study.dto.CreateStudyRequest;
import com.preclinical.study.dto.StudyPhaseView;
import com.preclinical.study.dto.StudyView;
import com.preclinical.study.dto.UpdateStudyRequest;
import com.preclinical.study.entity.Study;
import com.preclinical.study.entity.StudyPhase;

/**
 * Amazon/Netflix-style mapper — code is auto-generated at compile time.
 */
@Mapper(componentModel = "spring")
public interface StudyMapper {

	// Entity -> View
    @Mapping(target = "phases", source = "phases")
    StudyView toView(Study entity);

    // View -> Entity (for reverse mapping)
    @InheritInverseConfiguration
    Study toEntity(StudyView dto);

    // Create request -> Entity
    @Mapping(target = "studyId", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", constant = "0")
    @Mapping(target = "phases", ignore = true)
    Study fromCreateRequest(CreateStudyRequest dto);

    // Update request -> partial Entity
    @Mapping(target = "studyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "phases", ignore = true)
    Study fromUpdateRequest(UpdateStudyRequest dto);

    // Phase mapping
    StudyPhaseView toPhaseView(StudyPhase phase);
    StudyPhase toPhaseEntity(StudyPhaseView dto);

    List<StudyView> toViewList(List<Study> studies);
    
    // Use for patch-like updates
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateStudyRequest dto, @MappingTarget Study entity);
}
