package feign.reactor;


import feign.InvocationHandlerFactory.MethodHandler;
import feign.Target;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

final class ReactorInvocationHandler implements InvocationHandler {

    private final Target<?> target;
    private final Map<Method, MethodHandler> dispatch;

    ReactorInvocationHandler(Target<?> target, Map<Method, MethodHandler> dispatch) {
        this.target = requireNonNull(target, "target");
        this.dispatch = requireNonNull(dispatch, "dispatch");
    }


    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if ("equals".equals(method.getName())) {
            try {
                boolean hasArgs = args.length > 0 && args[0] != null;
                Object otherHandler = hasArgs ? Proxy.getInvocationHandler(args[0]) : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return hashCode();
        } else if ("toString".equals(method.getName())) {
            return toString();
        }

        Class<?> returnType = method.getReturnType();
        if (Flux.class.isAssignableFrom(returnType)) {
            return Flux.from(createMono(method, args));
        } else if (Mono.class.isAssignableFrom(returnType) ||
                Publisher.class.isAssignableFrom(returnType)) {
            return createMono(method, args);
        } else if (CompletableFuture.class.isAssignableFrom(returnType)) {
            return createMono(method, args).toFuture();
        }
        return this.dispatch.get(method).invoke(args);
    }

    private Mono<Object> createMono(Method method, Object[] args) {
        return Mono.defer(() -> {
            try {
                Object value = this.dispatch.get(method).invoke(args);
                return Mono.just(value);
            } catch (Throwable throwable) {
                return Mono.error(throwable);
            }
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReactorInvocationHandler) {
            ReactorInvocationHandler other = (ReactorInvocationHandler) obj;
            return target.equals(other.target);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public String toString() {
        return target.toString();
    }
}
