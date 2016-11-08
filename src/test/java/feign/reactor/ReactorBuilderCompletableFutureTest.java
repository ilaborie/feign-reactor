package feign.reactor;

import feign.Headers;
import feign.RequestLine;
import feign.gson.GsonDecoder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static feign.assertj.MockWebServerAssertions.assertThat;

public class ReactorBuilderCompletableFutureTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void testCompletableFuture() {
        server.enqueue(new MockResponse().setBody("\"foo\""));
        TestInterface api = target();

        CompletableFuture<String> future = api.completableFuture();

        assertThat(future).isNotNull();
        assertThat(future.join()).isEqualTo("foo");
    }

    @Test
    public void testCompletableFutureList() throws ExecutionException, InterruptedException {
        server.enqueue(new MockResponse().setBody("[\"foo\",\"bar\"]"));
        TestInterface api = target();

        CompletableFuture<List<String>> future = api.listCompletableFuture();

        assertThat(future.get()).isNotNull().containsExactly("foo", "bar");
    }

    @Test
    public void testCompletableFutureInt() {
        server.enqueue(new MockResponse().setBody("42"));
        TestInterface api = target();

        CompletableFuture<Integer> future = api.intCompletableFuture();

        assertThat(future).isNotNull();
        assertThat(future.join()).isEqualTo(42);
    }


    private TestInterface target() {
        return ReactorFeign.builder()
                .decoder(new GsonDecoder())
                .target(TestInterface.class, "http://localhost:" + server.getPort());
    }


    interface TestInterface {
        @RequestLine("GET /")
        @Headers("Accept: application/json")
        CompletableFuture<List<String>> listCompletableFuture();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        CompletableFuture<String> completableFuture();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        CompletableFuture<Integer> intCompletableFuture();
    }

}
