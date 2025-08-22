package com.bytebrain.integration;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class SecurityIntegrationTest {

    @Test
    @DisplayName("Test public endpoint accessibility")
    public void testPublicEndpoint() {
        given()
            .when()
            .get("/hello")
            .then()
            .statusCode(200)
            .body(is("Hello from Quarkus REST"));
    }

    @Test
    @DisplayName("Test protected endpoint requires authentication")
    public void testProtectedEndpointRequiresAuth() {
        given()
            .when()
            .get("/hello/secure")
            .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("Test user profile endpoint requires authentication")
    public void testUserProfileRequiresAuth() {
        given()
            .when()
            .get("/auth/me")
            .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("Test CORS headers are present")
    public void testCorsHeaders() {
        given()
            .header("Origin", "http://localhost:3000")
            .when()
            .get("/hello")
            .then()
            .statusCode(200)
            .header("Access-Control-Allow-Origin", "http://localhost:3000");
    }

    @Test
    @DisplayName("Test OAuth redirect URLs are properly formatted")
    public void testGitHubOAuthRedirectFormat() {
        given()
            .queryParam("state", "test-state-123")
            .when()
            .get("/auth/github")
            .then()
            .statusCode(307)
            .header("Location", containsString("github.com/login/oauth/authorize"))
            .header("Location", containsString("scope=user%3Aemail"));
    }

    @Test
    @DisplayName("Test Facebook OAuth redirect URLs are properly formatted")
    public void testFacebookOAuthRedirectFormat() {
        given()
            .queryParam("state", "test-state-456")
            .when()
            .get("/auth/facebook")
            .then()
            .statusCode(307)
            .header("Location", containsString("facebook.com"))
            .header("Location", containsString("scope=email%2Cpublic_profile"));
    }

    @Test
    @DisplayName("Test error handling in OAuth callbacks")
    public void testOAuthErrorHandling() {
        // Test GitHub error handling
        given()
            .queryParam("error", "access_denied")
            .queryParam("error_description", "User denied access")
            .when()
            .get("/auth/github/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("error=access_denied"));

        // Test Facebook error handling
        given()
            .queryParam("error", "user_denied")
            .queryParam("error_description", "User denied the request")
            .when()
            .get("/auth/facebook/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("error=user_denied"));
    }

    @Test
    @DisplayName("Test missing authorization code handling")
    public void testMissingCodeHandling() {
        // Test GitHub missing code
        given()
            .queryParam("state", "test-state")
            .when()
            .get("/auth/github/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("error=missing_code"));

        // Test Facebook missing code
        given()
            .queryParam("state", "test-state")
            .when()
            .get("/auth/facebook/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("error=missing_code"));
    }
}
