package assignment.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static assignment.fixtures.TestFixtures.generateAccessToken;
import static assignment.fixtures.TestFixtures.generateRefreshToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "rateLimit.enabled=false", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EligibilityControllerTest {

    @LocalServerPort
    private int port;

    private String authorization;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;

        var futureExpirationDate = Date.from(Instant.now().plusSeconds(1000));
        authorization = "Bearer " + generateAccessToken(futureExpirationDate);

        var futureExpirationDatee = Date.from(Instant.now().plusSeconds(999999999));
        System.out.println("aaa:" + generateAccessToken(futureExpirationDatee));
    }

    @Test
    @DisplayName("should return 200 status code with eligible token")
    void eligibleToken() {
        var futureExpirationDate = Date.from(Instant.now().plusSeconds(1000));
        var validAccessToken = generateAccessToken(futureExpirationDate);

        var requestBody = """
                {
                    "token": "%s"
                }
                """.formatted(validAccessToken);

        given()
                .contentType("application/json")
                .header("Authorization", authorization)
                .body(requestBody)
                .when()
                .post("/api/eligibility/verify")
                .then()
                .statusCode(200)
                .body("eligible", is(true))
                .body("user", is("user"))
                .body("tokenType", is("access_token"))
                .body("roles", is(List.of("ROLE_API_USER")));
    }

    @Test
    @DisplayName("should return success for valid refresh token")
    void validRefreshToken() {
        var expiredTokenDate = Date.from(Instant.now().plusSeconds(1000));
        var refreshToken = generateRefreshToken(expiredTokenDate);

        var requestBody = """
                {
                    "token": "%s"
                }
                """.formatted(refreshToken);

        given()
                .contentType("application/json")
                .header("Authorization", authorization)
                .body(requestBody)
                .when()
                .post("/api/eligibility/verify")
                .then()
                .statusCode(200)
                .body("eligible", is(true))
                .body("user", is("user"))
                .body("tokenType", is("refresh_token"))
                .body("roles", is(List.of("ROLE_API_USER")));
    }

    @Test
    @DisplayName("should return error code for expired token")
    void expiredToken() {
        var expiredTokenDate = Date.from(Instant.now().minusSeconds(1000));
        var expiredAccessToken = generateAccessToken(expiredTokenDate);

        var requestBody = """
                {
                    "token": "%s"
                }
                """.formatted(expiredAccessToken);

        given()
                .contentType("application/json")
                .header("Authorization", authorization)
                .body(requestBody)
                .when()
                .post("/api/eligibility/verify")
                .then()
                .statusCode(401)
                .body("message", containsString("JWT expired"));
    }

    @Test
    @DisplayName("should return unauthorized token")
    void invalidToken() {
        var requestBody = """
                {
                    "token": "notvalidtoken"
                }
                """;

        given()
                .contentType("application/json")
                .header("Authorization", authorization)
                .body(requestBody)
                .when()
                .post("/api/eligibility/verify")
                .then()
                .statusCode(401)
                .body("message", containsString("Invalid compact JWT string"));
    }

}
