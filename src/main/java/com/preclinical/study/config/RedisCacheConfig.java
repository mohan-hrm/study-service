package com.preclinical.study.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    // ObjectMapper for API responses (NO type information)
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.build();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // NO default typing - clean JSON for API responses
        return mapper;
    }

    // ObjectMapper specifically for Redis (WITH type information)
    @Bean
    @Qualifier("redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .allowIfSubType("com.preclinical.study")
                .build();
        
        // Enable type information ONLY for Redis
        mapper.activateDefaultTyping(
                ptv,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        
        return mapper;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration();
        redisConf.setHostName(redisHost);
        redisConf.setPort(redisPort);
        if (redisPassword != null && !redisPassword.isEmpty()) {
            redisConf.setPassword(redisPassword);
        }
        log.info("🔗 Connecting to Redis at {}:{}", redisHost, redisPort);
        return new LettuceConnectionFactory(redisConf);
    }

    private RedisCacheConfiguration createCacheConfig(ObjectMapper redisObjectMapper, Duration ttl) {
        GenericJackson2JsonRedisSerializer serializer = 
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);
        
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, 
                                         Environment env,
                                         @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("studyCache",
                ttlConfig(env, redisObjectMapper, "custom.cache.ttl.studyCache", 60));
        cacheConfigurations.put("studyListCache",
                ttlConfig(env, redisObjectMapper, "custom.cache.ttl.studyListCache", 10));
        cacheConfigurations.put("studyStatusCache",
                ttlConfig(env, redisObjectMapper, "custom.cache.ttl.studyStatusCache", 5));
        cacheConfigurations.put("studyDateRangeCache",
                ttlConfig(env, redisObjectMapper, "custom.cache.ttl.studyDateRangeCache", 30));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(createCacheConfig(redisObjectMapper, Duration.ofMinutes(30)))
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    private RedisCacheConfiguration ttlConfig(Environment env, ObjectMapper redisObjectMapper, 
                                             String key, int defaultMinutes) {
        long minutes = env.getProperty(key, Long.class, (long) defaultMinutes);
        return createCacheConfig(redisObjectMapper, Duration.ofMinutes(minutes));
    }
}