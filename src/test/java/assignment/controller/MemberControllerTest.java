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

import static assignment.fixtures.TestFixtures.generateAccessToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(properties = "rateLimit.enabled=false", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerTest {

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
    @DisplayName("should return 404 when no data is matching in db")
    void noRecordFound() {
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
                .statusCode(404)
                .body("message", is("No record is found with the given data"));
    }

    @Test
    @DisplayName("should return 403 when employee code is not matching with db employee code")
    void forbiddenEmployeeCode() {
        var requestBody =
                """
                {
                    "employeeCode": "wrongCode",
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
                .statusCode(403)
                .body("message", is("Employee code does not match"));
    }

    @Test
    @DisplayName("should return 200 with the found employee")
    void findEmployee() {
        var requestBody =
                """
                {
                    "employeeCode": "GROUP1",
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
                .statusCode(200)
                .body("status", is("success"))
                .body("data.memberUniqueId", is("EMP001"))
                .body("data.firstName", is("John"))
                .body("data.lastName", is("Doe"))
                .body("data.dateOfBirth", is("1980-01-15"))
                .body("data.eligibilityStartDate", is("2010-06-01"))
                .body("data.eligibilityEndDate", is("2025-12-31"))
                .body("data.employeeStatus", is("employee"))
                .body("data.employeeGroup", is("GROUP1"));
    }

    @Test
    @DisplayName("should return 200 with the found dependent")
    void findDependent() {
        var requestBody =
                """
                {
                    "employeeCode": "GROUP2",
                    "employeeId": "DEP001",
                    "memberStatus": "dependent",
                    "employeeDateOfBirth": "2010-05-20"
                }
                """;

        given()
                .contentType("application/json")
                .header("Authorization", authorization)
                .body(requestBody)
                .when()
                .post("/api/members/search")
                .then()
                .statusCode(200)
                .body("status", is("success"))
                .body("data.memberUniqueId", is("DEP001"))
                .body("data.firstName", is("Jane"))
                .body("data.lastName", is("Doe"))
                .body("data.dateOfBirth", is("2010-05-20"))
                .body("data.eligibilityStartDate", is("2020-01-01"))
                .body("data.eligibilityEndDate", is("2025-12-31"))
                .body("data.employeeStatus", is("dependent"))
                .body("data.employeeGroup", is("GROUP2"));
    }


}
