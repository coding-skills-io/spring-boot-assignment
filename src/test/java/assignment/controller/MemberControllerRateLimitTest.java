package assignment.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Instant;
import java.util.Date;

import static assignment.fixtures.TestFixtures.generateAccessToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerRateLimitTest {

    @LocalServerPort
    private int port;

    private String authorization;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;

        var accessToken = generateAccessToken(Date.from(Instant.now().plusSeconds(1000)));

        authorization = "Bearer " + accessToken;
    }

    @ParameterizedTest
    @CsvSource({
            // First ten requests: expected to return 404 and the "not found" message
            "404, No record is found with the given data",
            "404, No record is found with the given data",
            "404, No record is found with the given data",
            "404, No record is found with the given data",
            "404, No record is found with the given data",
            "404, No record is found with the given data",
            "404, No record is found with the given data",
            "404, No record is found with the given data",
            "404, No record is found with the given data",
            "404, No record is found with the given data",
            // Subsequent requests (11th, 12th, 13th): expected to be rate limited
            "429, Too many requests",
            "429, Too many requests",
            "429, Too many requests"
    })
    @DisplayName("should return 429 after too many attempts")
    void tooManyRequests(int expectedStatus, String expectedMessage) {
        var requestBody =
                """
                {
                    "employeeCode": "code",
                    "employeeId": "validId",
                    "memberStatus": "employee",
                    "employeeDateOfBirth": "1995-01-01",
                    "employeeFirstName": "John",
                    "employeeLastName": "Smith"
                }
                """;

        given()
                .contentType("application/json")
                .header("Authorization", authorization)
                .body(requestBody)
                .when()
                .post("/api/members/search")
                .then()
                .statusCode(expectedStatus)
                .body("message", is(expectedMessage));
    }

}
