## How to 

Use @CacheMono on the method you want to cache

Create a ReactiveCacheProvider that will have 2 methods. Lookup and Save, using your cache provider

If you want to cache on Redis you can use the ReactiveRedisCacheProvider (see below)

## How it works

The application is divided in this way:

* Annotation: the AOP definition of the @CacheMono
* Config: timeout configurations and beans 
* Providers: your own cache provider that will save or check if value exists. At the moment Redis and InMemory provider is implemented
* CacheService: This will use your Provider to check (and return) if the method is already cached, otherwise it will cache it.

## ReactiveCacheProvider
a ReactiveCacheProvider is an interface with a lookup and a save, that needs to be implemented by your cache provider.

eg. in the providers package you will find a ReactiveRedisCacheProvider. This has a reactiveRedisTemplate that will get/save the object of the cache.

```
public class ReactiveRedisCacheProvider implements ReactiveCacheProvider {

    // https://spring.io/guides/gs/spring-data-reactive-redis/
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
```

To use this cache simply create a new ReactiveCacheProvider bean (you should provide at least a ReactiveRedisTemplate bean)

    @Bean
    ReactiveCacheProvider cacheProvider(ReactiveRedisTemplate<String, Object> provider) {
        return new ReactiveRedisCacheProvider(provider, Duration.ofMinutes(30), "prefix");
    }

The default is a simple in memory cache (defined in CacheConfigurations)

## Configs
Simple object that contains the timeout of the save and lookup

## CacheService
This service will take a ReactiveCacheProvider and a Configs object. 

When the annotated method (@CacheMono) is invoked the service will check if the response is already cache (reactiveCacheProvider.lookup).
If it's not cached it will run the code and save the response (reactiveCacheProvider.save)

The Config object will add a timeout on lookup and save

## CacheConfigurations

All the beans configurations happens here

## Annotations

### CacheMono
Annotation that you need to use on your method.
The cache will work only for methods that return a Mono type it won't work if called in same class (See Spring AOP)

```
@CacheMono
public Mono<String> test(String arg) {
    // your method
}
```

### ReactiveCache
This class will intercept your annotated method and pass the called args in the CacheService.
 