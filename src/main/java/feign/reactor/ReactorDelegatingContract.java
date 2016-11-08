package feign.reactor;

import feign.Contract;
import feign.MethodMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

import static feign.Util.resolveLastTypeParameter;
import static java.util.stream.Collectors.toList;

/**
 * This special cases methods that return {@link Flux}, {@link Mono}, or {@link CompletableFuture} so that they
 * are decoded properly.
 * <p>
 * <p>For example, {@literal Mono<Foo>} and {@literal CompletableFuture<Foo>} will decode {@code Foo}.
 */
final class ReactorDelegatingContract implements Contract {

    private final Contract delegate;

    ReactorDelegatingContract(Contract delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<MethodMetadata> parseAndValidatateMetadata(Class<?> targetType) {
        return this.delegate.parseAndValidatateMetadata(targetType).stream()
                .map(handleParametrizedType(Flux.class))
                .map(handleParametrizedType(Mono.class))
                .map(handleParametrizedType(CompletableFuture.class))
                .collect(toList());
    }

    private UnaryOperator<MethodMetadata> handleParametrizedType(Class<?> clazz) {
        return metadata -> {
            Type type = metadata.returnType();
            if (type instanceof ParameterizedType &&
                    ((ParameterizedType) type).getRawType().equals(clazz)) {
                Type actualType = resolveLastTypeParameter(type, clazz);
                metadata.returnType(actualType);
            }
            return metadata;
        };
    }
}