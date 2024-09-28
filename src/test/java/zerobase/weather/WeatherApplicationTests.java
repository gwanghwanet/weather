package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class WeatherApplicationTests {

    @Test
    void sampleTest() {
        assertThat(1, anything());
    }

    @Test
    void equalTest() {
        assertThat(1, equalTo(1));
    }

    @Test
    void failTest() {
        assertThat(1, equalTo(2));
    }
}
