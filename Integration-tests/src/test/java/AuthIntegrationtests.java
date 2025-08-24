import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationtests {

    @BeforeAll
    public static void setup()
    {
        RestAssured.baseURI="http://localhost:6004";
    }


    @Test
    public void shouldReturnJWTTokenOk(){
        String payload = """
                {
                    "email":"kkr@gmail.com",
                    "password":"khagesh"
                }
                """;

         given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token",notNullValue())
                .extract().response();
    }


    @Test
    public void unauthorizedUserShouldReturn401(){
        String payload = """
                {
                    "email":"kkr@gmail.com",
                    "password":"kkr"
                }
                """;

        given().contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401);
    }
}
