package com.example.cacheable.cache.annotations;

import com.example.cacheable.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Aspect
@Slf4j
@Component
public class ReactiveCache {

    final CacheService cacheService;

    public ReactiveCache(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Around("@annotation(com.example.cacheable.cache.annotations.CacheMono)")
    public Mono<?> cache(ProceedingJoinPoint joinPoint) {
        return cacheService.getOrCache(joinPoint);
    }
}
