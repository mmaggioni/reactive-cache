package com.example.cacheable.cache;

import com.example.cacheable.cache.config.Configs;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class CacheService {

    private ReactiveCacheProvider reactiveCacheProvider;

    private final Configs configs;

    public Mono<?> getOrCache(ProceedingJoinPoint joinPoint) {

        List<Object> request = new ArrayList<>();

        Collections.addAll(request, joinPoint.getArgs());

        return Mono.just(request)
                .flatMap(reqList -> lookup(request))
                .switchIfEmpty(proceedAndSave(joinPoint, request));
    }

    private Mono<Object> lookup(List<Object> request) {
        return reactiveCacheProvider.lookup(request)
                .name("reactive.cache.lookup")
                .metrics()
                .doOnSuccess(o -> log.debug("cache found: " + o))
                .timeout(Duration.ofMillis(configs.lookupTimeout), Mono.fromCallable(() -> {
                    log.warn("Timeout on lookup cache {}", request);
                    return Mono.empty();
                }))
                .onErrorResume(throwable -> {
                    log.warn("Error while lookup cache" + throwable.getMessage());
                    return Mono.empty();
                });
    }

    @SneakyThrows
    private Mono<?> proceedAndSave(ProceedingJoinPoint joinPoint, List<Object> request) {
        Mono<?> result = (Mono) joinPoint.proceed();

        return result.doOnSuccess(response ->
                reactiveCacheProvider.save(request, response)
                        .name("reactive.cache.lookup")
                        .metrics()
                        .doOnSuccess(o -> log.debug("cached result: {}", response))
                        .timeout(Duration.ofMillis(configs.lookupTimeout), Mono.defer(() -> {
                            log.warn("Timeout while caching the response {}", response);
                            return Mono.empty();
                        }))
                        .doOnError(throwable -> log.debug("Error while saving the response", throwable))
                        .onErrorResume(throwable -> Mono.empty())
                        .subscribe()
        );
    }

//    public void clearAll() {
//        log.info("Clearing all tmp cache...");
//        redisTemplate.getConnectionFactory().getReactiveConnection().serverCommands().flushDb().subscribe();
//    }

}
