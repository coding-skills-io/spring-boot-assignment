package assignment.controller;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Instant;
import java.util.Date;

import static assignment.fixtures.TestFixtures.generateAccessToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "rateLimit.enabled=false", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerValidatorTest {

    @LocalServerPort
    private int port;

    private String authorization;

    @BeforeAll
    void setUp() {
        RestAssured.port = port;

        var accessToken = generateAccessToken(Date.from(Instant.now().plusSeconds(1000)));

        authorization = "Bearer " + accessToken;
    }


    @Test
    @DisplayName("should return 400 bad request when employee code is empty")
    void emptyEmployeeCode() {
        var requestBody =
                """
                {
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
                .statusCode(400)
                .body("message", hasSize(1))
                .body("message[0]", is("employeeCode is required"));
    }

    @Test
    @DisplayName("should return 400 when employee code is invalid")
    void invalidEmployeeCode() {
        var requestBody =
                """
                {
                    "employeeCode": "#code",
                    "employeeId": "EMP001",
                    "memberStatus": "employee",
                    "employeeDateOfBirth": "1980-01-15",
                    "employeeFirstName": "John",
                    "employeeLastName": "Doe"
                }
                """;

        given()
                .contentType("application/json")
                .header("Authorization", authorization)
                .body(requestBody)
                .when()
                .post("/api/members/search")
                .then()
                .statusCode(400)
                .body("message", hasSize(1))
                .body("message[0]", Matchers.is("employeeCode only support alphanumeric characters"));
    }

    @Test
    @DisplayName("should return 400 when employee code is too long")
    void longEmployeeCode() {
        var requestBody =
                """
                {
                    "employeeCode": "veryverylongcode",
                    "employeeId": "EMP001",
                    "memberStatus": "employee",
                    "employeeDateOfBirth": "1980-01-15",
                    "employeeFirstName": "John",
                    "employeeLastName": "Doe"
                }
                """;

        given()
                .contentType("application/json")
                .header("Authorization", authorization)
                .body(requestBody)
                .when()
                .post("/api/members/search")
                .then()
                .statusCode(400)
                .body("message", hasSize(1))
                .body("message[0]", Matchers.is("employeeCode supports min 1 and max 10 characters"));
    }

    @Test
    @DisplayName("should return 400 when employee id too long")
    void invalidEmployeeIdLength() {
        var requestBody =
                """
                {
                    "employeeCode": "EMP001$$",
                    "employeeId": "EMP001",
                    "memberStatus": "employee",
                    "employeeDateOfBirth": "1980-01-15",
                    "employeeFirstName": "John",
                    "employeeLastName": "Doe"
                }
                """;

        given()
                .contentType("application/json")
                .header("Authorization", authorization)
                .body(requestBody)
                .when()
                .post("/api/members/search")
                .then()
                .statusCode(400)
                .body("message", hasSize(1))
                .body("message[0]", is("employeeCode only support alphanumeric characters"));
    }

    @Test
    @DisplayName("should return 400 when invalid firstname passed")
    void invalidFirstName() {
        var requestBody =
                """
                {
                    "employeeCode": "code",
                    "employeeId": "validId",
                    "memberStatus": "employee",
                    "employeeDateOfBirth": "1995-01-01",
                    "employeeFirstName": "2dd",
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
                .statusCode(400)
                .body("message", hasSize(1))
                .body("message[0]", is("employeeFirstName should be only letters or space"));
    }

    @Test
    @DisplayName("should return 400 when invalid firstname passed")
    void invalidLastName() {
        var requestBody =
                """
                {
                    "employeeCode": "code",
                    "employeeId": "validId",
                    "memberStatus": "employee",
                    "employeeDateOfBirth": "1995-01-01",
                    "employeeFirstName": "John",
                    "employeeLastName": "S1mith"
                }
                """;

        given()
                .contentType("application/json")
                .header("Authorization", authorization)
                .body(requestBody)
                .when()
                .post("/api/members/search")
                .then()
                .statusCode(400)
                .body("message", hasSize(1))
                .body("message[0]", is("employeeLastName should be only letters or space"));
    }

    @Test
    @DisplayName("should return 400 when dob has invalid format")
    void invalidDobFormat() {
        var requestBody =
                """
                {
                    "employeeCode": "code",
                    "employeeId": "validId",
                    "memberStatus": "employee",
                    "employeeDateOfBirth": "01-01-1995",
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
                .statusCode(400)
                .body("message", hasSize(1))
                .body("message[0]", is("employeeDateOfBirth should be in ISO 8601 format"));
    }

    @Test
    @DisplayName("should return 400 when members status is not employee or dependent")
    void invalidMemberStatus() {
        var requestBody =
                """
                {
                    "employeeCode": "code",
                    "employeeId": "validId",
                    "memberStatus": "admin",
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
                .statusCode(400)
                .body("message", hasSize(1))
                .body("message[0]", is("memberStatus only supports 'employee' or 'dependent' values"));
    }

    @Test
    @DisplayName("should return multiple error messages")
    void multipleErrorMessages() {
        var requestBody =
                """
                {
                    "employeeCode": "code",
                    "employeeId": "validId",
                    "memberStatus": "admin",
                    "employeeDateOfBirth": "1995-01-01",
                    "employeeFirstName": "John",
                    "employeeLastName": "12Smith"
                }
                """;

        given()
                .contentType("application/json")
                .header("Authorization", authorization)
                .body(requestBody)
                .when()
                .post("/api/members/search")
                .then()
                .statusCode(400)
                .body("message", hasSize(2))
                .body("message", containsInAnyOrder("employeeLastName should be only letters or space", "memberStatus only supports 'employee' or 'dependent' values"));
    }


}
