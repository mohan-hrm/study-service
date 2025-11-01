package com.preclinical.study.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut: all methods inside controller & service packages
    @Around("execution(* com.preclinical.study.controller..*(..)) || execution(* com.preclinical.study.service..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();
        log.info("➡️ Entering {}", method);

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("✅ Exiting {} | Time: {} ms", method, duration);
            return result;
        } catch (Throwable ex) {
            log.error("❌ Exception in {}: {}", method, ex.getMessage(), ex);
            throw ex;
        }
    }
}