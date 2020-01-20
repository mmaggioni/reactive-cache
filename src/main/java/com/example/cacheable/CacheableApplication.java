package com.example.cacheable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CacheableApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheableApplication.class, args);
    }
}
