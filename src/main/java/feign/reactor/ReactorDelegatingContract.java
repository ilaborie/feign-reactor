package feign.reactor;

import feign.Contract;
import feign.MethodMetadata;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static feign.Util.resolveLastTypeParameter;

/**
 * This special cases methods that return {@link Flux}, {@link Mono}, {@link Publisher}, or {@link CompletableFuture} so that they
 * are decoded properly.
 * <p>
 * <p>For example, {@literal Mono<Foo>} and {@literal CompletableFuture<Foo>} will decode {@code Foo}.
 */
public final class ReactorDelegatingContract implements Contract {

    private final Contract delegate;

    public ReactorDelegatingContract(Contract delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<MethodMetadata> parseAndValidatateMetadata(Class<?> targetType) {
        List<MethodMetadata> metadatas = this.delegate.parseAndValidatateMetadata(targetType);

        for (MethodMetadata metadata : metadatas) {
            Type type = metadata.returnType();

            if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(Mono.class)) {
                Type actualType = resolveLastTypeParameter(type, Mono.class);
                metadata.returnType(actualType);
            } else if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(Flux.class)) {
                Type actualType = resolveLastTypeParameter(type, Flux.class);
                metadata.returnType(actualType);
            } else if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(Publisher.class)) {
                Type actualType = resolveLastTypeParameter(type, Publisher.class);
                metadata.returnType(actualType);
            } else if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(CompletableFuture.class)) {
                Type actualType = resolveLastTypeParameter(type, CompletableFuture.class);
                metadata.returnType(actualType);
            }
        }

        return metadatas;
    }
}
