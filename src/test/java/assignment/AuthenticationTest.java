package assignment;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import javax.crypto.SecretKey;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private SecretKey key;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("should return 403 for requests with invalid credentials")
    void invalidUsernamePassword() {
        var authenticationBody = """
        {
            "username": "nonExistentUsername",
            "password": "password"
        }
        """;

        given()
                .contentType("application/json")
                .body(authenticationBody)
                .when()
                .post("/api/login")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("should return access and refresh tokens and set header with AccessToken")
    void authenticate() {
        var authenticationBody = """
        {
            "username": "user",
            "password": "password"
        }
        """;

        var response = given()
                .contentType("application/json")
                .body(authenticationBody)
                .when()
                .post("/api/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("accessToken", not(emptyString()))
                .body("refreshToken", not(emptyString()))
                .extract().response();

        String accessToken = response.path("accessToken");
        String refreshToken = response.path("refreshToken");

        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer " + accessToken);

        assertThat(parseClaims(accessToken)).satisfies(claims -> {
            assertThat(claims.getSubject()).isEqualTo("user");
            assertThat(claims.getIssuer()).isEqualTo("spring-boot-assignment");
            assertThat(claims.get("token_type")).isEqualTo("access_token");
            assertThat(claims.get("roles")).isEqualTo(List.of("ROLE_API_USER"));
            assertThat(claims.getExpiration()).isNotNull();
            assertThat(claims.getIssuedAt()).isNotNull();
        });

        assertThat(parseClaims(refreshToken)).satisfies(claims -> {
            assertThat(claims.getSubject()).isEqualTo("user");
            assertThat(claims.getIssuer()).isEqualTo("spring-boot-assignment");
            assertThat(claims.get("roles")).isEqualTo(List.of("ROLE_API_USER"));
            assertThat(claims.getExpiration()).isNotNull();
            assertThat(claims.getIssuedAt()).isNotNull();
            assertThat(claims.get("token_type")).isEqualTo("refresh_token");
        });
    }

    private Claims parseClaims(final String jwt) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

}
