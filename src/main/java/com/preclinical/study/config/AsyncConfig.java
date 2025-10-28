package com.preclinical.study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("study-scheduler-");
        scheduler.initialize();
        return scheduler;
    }
    
    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);           // always alive threads
        executor.setMaxPoolSize(10);           // max threads under load
        executor.setQueueCapacity(25);         // queued tasks
        executor.setThreadNamePrefix("mail-");
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "eventExecutor")
    public Executor eventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);           // always alive threads
        executor.setMaxPoolSize(10);           // max threads under load
        executor.setQueueCapacity(25);         // queued tasks
        executor.setThreadNamePrefix("event-");
        executor.initialize();
        return executor;
    }
}
