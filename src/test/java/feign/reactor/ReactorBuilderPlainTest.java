package feign.reactor;

import feign.Headers;
import feign.RequestLine;
import feign.Target;
import feign.Target.HardCodedTarget;
import feign.gson.GsonDecoder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static feign.assertj.MockWebServerAssertions.assertThat;

public class ReactorBuilderPlainTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void plainString() {
        server.enqueue(new MockResponse().setBody("\"foo\""));
        TestInterface api = target();

        String string = api.get();
        assertThat(string).isEqualTo("foo");
    }

    @Test
    public void plainList() {
        server.enqueue(new MockResponse().setBody("[\"foo\",\"bar\"]"));
        TestInterface api = target();

        List<String> list = api.getList();

        assertThat(list).isNotNull().containsExactly("foo", "bar");
    }

    @Test
    public void plainInt() {
        server.enqueue(new MockResponse().setBody("42"));
        TestInterface api = target();

        Integer value = api.getInt();

        assertThat(value).isEqualTo(42);
    }


    @Test
    public void equalsHashCodeAndToStringWork() {
        Target<TestInterface> t1 =
                new HardCodedTarget<>(TestInterface.class, "http://localhost:8080");
        Target<TestInterface> t2 =
                new HardCodedTarget<>(TestInterface.class, "http://localhost:8888");
        Target<OtherTestInterface> t3 =
                new HardCodedTarget<>(OtherTestInterface.class, "http://localhost:8080");
        TestInterface i1 = ReactorFeign.builder().target(t1);
        TestInterface i2 = ReactorFeign.builder().target(t1);
        TestInterface i3 = ReactorFeign.builder().target(t2);
        OtherTestInterface i4 = ReactorFeign.builder().target(t3);

        assertThat(i1)
                .isEqualTo(i2)
                .isNotEqualTo(i3)
                .isNotEqualTo(i4);

        assertThat(i1.hashCode())
                .isEqualTo(i2.hashCode())
                .isNotEqualTo(i3.hashCode())
                .isNotEqualTo(i4.hashCode());

        assertThat(i1.toString())
                .isEqualTo(i2.toString())
                .isNotEqualTo(i3.toString())
                .isNotEqualTo(i4.toString());

        assertThat(t1)
                .isNotEqualTo(i1);

        assertThat(t1.hashCode())
                .isEqualTo(i1.hashCode());

        assertThat(t1.toString())
                .isEqualTo(i1.toString());
    }

    private TestInterface target() {
        return ReactorFeign.builder()
                .decoder(new GsonDecoder())
                .target(TestInterface.class, "http://localhost:" + server.getPort());
    }

    interface OtherTestInterface {

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        List<String> list();
    }

    interface TestInterface {

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        String get();

        @RequestLine("GET /")
        @Headers("Accept: application/json")
        List<String> getList();

        @RequestLine("GET /")
        Integer getInt();
    }

}
