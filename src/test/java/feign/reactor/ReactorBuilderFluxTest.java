package feign.reactor;

import feign.Headers;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static feign.assertj.MockWebServerAssertions.assertThat;

public class ReactorBuilderFluxTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void testFlux() {
        server.enqueue(new MockResponse().setBody("\"foo\""));
        TestInterface api = target();

        Flux<String> flux = api.flux();

        assertThat(flux).isNotNull();
        StepVerifier.create(flux)
                .expectNext("foo")
                .expectComplete()
                .verify();
    }

    @Test
    public void testFluxList() {
        server.enqueue(new MockResponse().setBody("[\"foo\",\"bar\"]"));
        TestInterface api = target();

        Flux<List<String>> flux = api.listFlux();

        assertThat(flux).isNotNull();
        StepVerifier.create(flux)
                .expectNextMatches(list -> {
                    assertThat(list).isNotNull().containsExactly("foo", "bar");
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void testFluxInt() {
        server.enqueue(new MockResponse().setBody("42"));
        TestInterface api = target();

        Flux<Integer> flux = api.intFlux();

        assertThat(flux).isNotNull();
        StepVerifier.create(flux)
                .expectNext(42)
                .expectComplete()
                .verify();
    }


    private TestInterface target() {
        return ReactorFeign.builder()
                .decoder(new GsonDecoder())
                .target(TestInterface.class, "http://localhost:" + server.getPort());
    }


    interface TestInterface {
        @RequestLine("GET /")
        @Headers("Accept: application/json")
        Flux<List<String>> listFlux();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        Flux<String> flux();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        Flux<Integer> intFlux();
    }

}
