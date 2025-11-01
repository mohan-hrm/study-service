package com.preclinical.study.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * Loads cache names and TTLs from cache-config.yaml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cache")
public class CacheConfigProperties {

    /**
     * Cache names mapping (logical name -> actual cache name)
     * Example: studyCache: studyCache
     */
    private Map<String, String> names;

    /**
     * Cache TTLs in minutes (logical name -> TTL duration)
     */
    private Map<String, Integer> ttl;
}
