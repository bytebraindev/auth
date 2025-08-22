package com.bytebrain.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(org.hamcrest.CoreMatchers.is("Hello from Quarkus REST"));
    }

    @Test
    public void testSecureHelloEndpointUnauthorized() {
        given()
          .when().get("/hello/secure")
          .then()
             .statusCode(401); // Should be unauthorized without JWT
    }
}
