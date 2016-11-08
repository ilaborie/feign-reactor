package feign.reactor;

import feign.Headers;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static feign.assertj.MockWebServerAssertions.assertThat;

public class ReactorBuilderMonoTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void testMono() {
        server.enqueue(new MockResponse().setBody("\"foo\""));
        TestInterface api = target();

        Mono<String> mono = api.mono();

        assertThat(mono).isNotNull();
        StepVerifier.create(mono)
                .expectNext("foo")
                .expectComplete()
                .verify();
    }

    @Test
    public void testMonoList() {
        server.enqueue(new MockResponse().setBody("[\"foo\",\"bar\"]"));
        TestInterface api = target();

        Mono<List<String>> mono = api.listMono();

        assertThat(mono).isNotNull();


        StepVerifier.create(mono)
                .expectNextMatches(list -> {
                    assertThat(list).isNotNull().containsExactly("foo", "bar");
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void testMonoInt() {
        server.enqueue(new MockResponse().setBody("42"));
        TestInterface api = target();

        Mono<Integer> mono = api.intMono();

        assertThat(mono).isNotNull();
        StepVerifier.create(mono)
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
        Mono<List<String>> listMono();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        Mono<String> mono();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        Mono<Integer> intMono();
    }

}
