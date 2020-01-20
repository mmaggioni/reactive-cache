package com.example.cacheable.cache.providers;

import com.example.cacheable.cache.ReactiveCacheProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@AllArgsConstructor
public class ReactiveRedisCacheProvider implements ReactiveCacheProvider {

    final ReactiveRedisTemplate<String, Object> redisTemplate;
    final Duration ttl;
    final String cachePrefix;

    public Mono<Object> lookup(Object request) {
        return redisTemplate.opsForValue()
                .get(cachePrefix + "::" + request.toString());
    }

    public Mono<Boolean> save(Object request, Object response) {
        return redisTemplate.opsForValue()
                .set(cachePrefix + "::" + request.toString(), response, ttl);
    }
}
