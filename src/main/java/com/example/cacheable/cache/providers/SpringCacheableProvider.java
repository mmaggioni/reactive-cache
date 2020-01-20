package com.example.cacheable.cache.providers;

import com.example.cacheable.cache.ReactiveCacheProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import reactor.core.publisher.Mono;

import java.util.Optional;

@AllArgsConstructor
public class SpringCacheableProvider implements ReactiveCacheProvider {

    private CacheManager cacheManager;

    @Value("${reactive.cache.name:rcache}")
    private String name;

    @Override
    public Mono<Object> lookup(Object request) {

        return getCache()
                .map(cache -> cache.get(request.toString()))
                .map(Cache.ValueWrapper::get)
                .map(Mono::justOrEmpty)
                .orElse(Mono.empty());
    }

    @Override
    public Mono<Boolean> save(Object request, Object response) {
        getCache().ifPresent(cache -> cache.put(request.toString(), response));
        return Mono.just(true);
    }

    private Optional<Cache> getCache() {
        return Optional.ofNullable(cacheManager.getCache(name));
    }
}
