package assignment.controller;

import assignment.controller.request.RefreshTokenRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Instant;
import java.util.Date;

import static assignment.fixtures.TestFixtures.MAPPER;
import static assignment.fixtures.TestFixtures.generateRefreshToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "rateLimit.enabled=false", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RefreshTokenControllerTest {

    @LocalServerPort
    private int port;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("should return new access token")
    void validRefreshToken() throws Exception {
        var futureExpirationDate = Date.from(Instant.now().plusSeconds(1000));
        var refreshToken = generateRefreshToken(futureExpirationDate);

        var request = new RefreshTokenRequest(refreshToken);

        given()
                .contentType("application/json")
                .body(MAPPER.writeValueAsString(request))
                .when()
                .post("/api/login/refresh")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("should return error when refresh token is expired")
    void expireRefreshToken() throws Exception {
        var futureExpirationDate = Date.from(Instant.now().minusSeconds(1000));
        var refreshToken = generateRefreshToken(futureExpirationDate);

        var request = new RefreshTokenRequest(refreshToken);

        given()
                .contentType("application/json")
                .body(MAPPER.writeValueAsString(request))
                .when()
                .post("/api/login/refresh")
                .then()
                .statusCode(401)
                .body("message", is("Invalid or expired refresh token"));
    }

    @Test
    @DisplayName("should return error when refresh token is invalid")
    void invalidRefreshToken() throws Exception {
        var request = new RefreshTokenRequest("invalid-refresh-token");

        given()
                .contentType("application/json")
                .body(MAPPER.writeValueAsString(request))
                .when()
                .post("/api/login/refresh")
                .then()
                .statusCode(401)
                .body("message", is("Invalid or expired refresh token"));
    }

}
