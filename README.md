Reactor core for Feign
===================
WARNING this module only support Java 8 !

This module wraps Feign's http requests in [Reactor core](https://github.com/reactor/reactor-core).

To use Reactor with Feign, add the Reactor module to your classpath. Then, configure Feign to use the `ReactorFeign`:

```java
GitHub github = ReactorFeign.builder()
        .target(GitHub.class, "https://api.github.com");
```

For asynchronous, return `CompletableFuture<YourType>`.

For Reactor compatibility or reactive use, return `Mono<YourType>` or `Flux<YourType>`, of package `reactor.core.publisher`. 

```java
interface YourApi {
  @RequestLine("GET /yourtype/{id}")
  CompletableFuture<YourType> getYourType(@Param("id") String id);

  @RequestLine("GET /yourtype/{id}")
  Mono<YourType> getYourTypeObservable(@Param("id") String id);

  @RequestLine("GET /yourtype/{id}")
  Flux<YourType> getYourTypeSingle(@Param("id") String id);

  @RequestLine("GET /yourtype/{id}")
  YourType getYourTypeSynchronous(@Param("id") String id);
}

YourApi api = ReactorFeign.builder()
                  .target(YourApi.class, "https://example.com");
```
