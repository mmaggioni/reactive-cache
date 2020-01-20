package com.example.cacheable.cache.config;

import com.example.cacheable.cache.CacheService;
import com.example.cacheable.cache.ReactiveCacheProvider;
import com.example.cacheable.cache.providers.SpringCacheableProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class CacheConfigurations {

    @Value("${reactive.cache.name:rcache}")
    private String name;

    @Bean
    @ConditionalOnMissingBean
    CacheService cacheService(ReactiveCacheProvider provider, Configs configs) {
        return new CacheService(provider, configs);
    }

    @Bean
    @ConditionalOnMissingBean
    ReactiveCacheProvider cacheProvider(CacheManager cacheManager) {
        return new SpringCacheableProvider(cacheManager, name);
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Collections.singletonList(new ConcurrentMapCache(name)));
        return cacheManager;
    }
}
