package com.preclinical.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.Validator;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
public class StudyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyServiceApplication.class, args);
	}

	@Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }
}
