package assignment;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CorsTest {

    @LocalServerPort
    private int port;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("fail when cors header is not localhost")
    void corsFailure() {
        given()
                .when()
                .header("Origin", "http://example.com")
                .get("/actuator/health")
                .then()
                .statusCode(403);
    }

}
