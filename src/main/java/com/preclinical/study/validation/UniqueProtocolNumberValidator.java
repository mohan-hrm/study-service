package com.preclinical.study.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.preclinical.study.repository.StudyRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueProtocolNumberValidator
        implements ConstraintValidator<UniqueProtocolNumber, String> {

	@Autowired
    private StudyRepository studyRepository;

    @Override
    public boolean isValid(String protocolNumber, ConstraintValidatorContext context) {
        if (protocolNumber == null || protocolNumber.isBlank()) return true; // handled by @NotBlank
        return !studyRepository.existsByProtocolNumber(protocolNumber);
    }
}
