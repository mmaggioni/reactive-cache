package com.example.cacheable.cache;

import reactor.core.publisher.Mono;

public interface ReactiveCacheProvider {

    Mono<Object> lookup(Object request);

    Mono<Boolean> save(Object request, Object response);
}
