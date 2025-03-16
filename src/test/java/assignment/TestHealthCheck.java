package assignment;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestHealthCheck {


    @LocalServerPort
    private int port;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("should return 200 OK for service health check")
    void serviceRuns() {
        given()
                .when()
                .get("/actuator/health")
                .then()
                .statusCode(200);
    }

}
