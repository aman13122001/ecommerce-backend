package com.ecommerce;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class GraphQLSecurityTest {

    @Test
    public void loginMutationShouldReturnToken() {
        String body = """
        {
          "query": "mutation { login(request: { email: \\"aman@test.com\\", password: \\"123456\\" }) }"
        }
        """;

        given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post("/graphql")
        .then()
            .statusCode(200);
    }
}
