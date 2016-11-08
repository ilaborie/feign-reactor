package feign.reactor;


import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;

/**
 * Allows Feign interfaces to return Mono, Flux Plublisher or CompletableFuture.
 */
public final class ReactorFeign {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends Feign.Builder {

        private Contract contract = new Contract.Default();


        @Override
        public Feign.Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder contract(Contract contract) {
            this.contract = contract;
            return this;
        }

        @Override
        public Feign build() {
            super.invocationHandlerFactory(ReactorInvocationHandler::new);
            super.contract(new ReactorDelegatingContract(contract));
            return super.build();
        }

        // Covariant overrides to support chaining to new fallback method.
        @Override
        public Builder logLevel(Logger.Level logLevel) {
            return (Builder) super.logLevel(logLevel);
        }

        @Override
        public Builder client(Client client) {
            return (Builder) super.client(client);
        }

        @Override
        public Builder retryer(Retryer retryer) {
            return (Builder) super.retryer(retryer);
        }

        @Override
        public Builder logger(Logger logger) {
            return (Builder) super.logger(logger);
        }

        @Override
        public Builder encoder(Encoder encoder) {
            return (Builder) super.encoder(encoder);
        }

        @Override
        public Builder decoder(Decoder decoder) {
            return (Builder) super.decoder(decoder);
        }

        @Override
        public Builder decode404() {
            return (Builder) super.decode404();
        }

        @Override
        public Builder errorDecoder(ErrorDecoder errorDecoder) {
            return (Builder) super.errorDecoder(errorDecoder);
        }

        @Override
        public Builder options(Request.Options options) {
            return (Builder) super.options(options);
        }

        @Override
        public Builder requestInterceptor(RequestInterceptor requestInterceptor) {
            return (Builder) super.requestInterceptor(requestInterceptor);
        }

        @Override
        public Builder requestInterceptors(Iterable<RequestInterceptor> requestInterceptors) {
            return (Builder) super.requestInterceptors(requestInterceptors);
        }
    }
}
