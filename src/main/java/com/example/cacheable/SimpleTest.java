package com.example.cacheable;

import com.example.cacheable.cache.annotations.CacheMono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Random;

@Component
public class SimpleTest {

    @Autowired
    InnerClass innerClass;

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {

        String t1 = innerClass.test("123123").block();
        String t2 = innerClass.test("33333").block();
        String t3 = innerClass.test("123123").block();

        // todo move in test
        System.out.println(t1.equals(t3)); // true
        System.out.println(t1.equals(t2)); // false
        System.out.println(t2.equals(t3)); // false

    }
}

@Component
class InnerClass {

    @CacheMono
    public Mono<String> test(String arg) {
        return Mono.just(arg + " - " + new Random().nextInt());
    }
}