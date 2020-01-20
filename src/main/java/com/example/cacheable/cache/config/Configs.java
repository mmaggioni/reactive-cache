package com.example.cacheable.cache.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Configs {

    @Value("${reactive.cache.lookup.timeout:300}")
    public Integer lookupTimeout;

    @Value("${reactive.cache.save.timeout:300}")
    public Integer saveTimeout;
}
