import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class UserIntegrationTests {


    @BeforeAll
    public static void setup() {
        RestAssured.baseURI="http://localhost:6004";
    }

    @Test
    public void shouldReturnOKResponse(){
        String login_payload = """
                {
                    "email":"kkr@gmail.com",
                    "password":"khagesh"
                }
                """;

       String token= given().contentType(ContentType.JSON)
                .body(login_payload)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .body().jsonPath().getString("token");

       given().
               contentType(ContentType.JSON).
               header("Authorization","Bearer "+token)
               .when()
               .get("/api/users-service/getUsers")
               .then()
               .statusCode(200);

    }

}
