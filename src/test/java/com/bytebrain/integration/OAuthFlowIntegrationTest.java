package com.bytebrain.integration;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class OAuthFlowIntegrationTest {

    @Test
    @DisplayName("GitHub OAuth Flow - Step 1: Initiate authentication")
    public void testGitHubOAuthInitiation() {
        given()
            .queryParam("state", "random-state-123")
            .when()
            .get("/auth/github")
            .then()
            .statusCode(307)
            .header("Location", containsString("github.com/login/oauth/authorize"))
            .header("Location", containsString("client_id="))
            .header("Location", containsString("state=random-state-123"));
    }

    @Test
    @DisplayName("GitHub OAuth Flow - Step 2: Handle callback with missing code")
    public void testGitHubCallbackMissingCode() {
        given()
            .queryParam("state", "random-state-123")
            .when()
            .get("/auth/github/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("error=missing_code"));
    }

    @Test
    @DisplayName("GitHub OAuth Flow - Step 2: Handle callback with error")
    public void testGitHubCallbackWithError() {
        given()
            .queryParam("error", "access_denied")
            .queryParam("error_description", "User denied access")
            .queryParam("state", "random-state-123")
            .when()
            .get("/auth/github/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("error=access_denied"));
    }

    @Test
    @DisplayName("Facebook OAuth Flow - Step 1: Initiate authentication")
    public void testFacebookOAuthInitiation() {
        given()
            .queryParam("state", "random-state-456")
            .when()
            .get("/auth/facebook")
            .then()
            .statusCode(307)
            .header("Location", containsString("facebook.com"))
            .header("Location", containsString("client_id="))
            .header("Location", containsString("state=random-state-456"))
            .header("Location", containsString("scope=email,public_profile"));
    }

    @Test
    @DisplayName("Facebook OAuth Flow - Step 2: Handle callback with missing code")
    public void testFacebookCallbackMissingCode() {
        given()
            .queryParam("state", "random-state-456")
            .when()
            .get("/auth/facebook/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("error=missing_code"));
    }

    @Test
    @DisplayName("Facebook OAuth Flow - Step 2: Handle callback with error")
    public void testFacebookCallbackWithError() {
        given()
            .queryParam("error", "access_denied")
            .queryParam("error_description", "User denied the request")
            .queryParam("state", "random-state-456")
            .when()
            .get("/auth/facebook/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("error=access_denied"));
    }

    @Test
    @DisplayName("Authentication Flow - Access protected endpoint without token")
    public void testProtectedEndpointWithoutToken() {
        given()
            .when()
            .get("/auth/me")
            .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("Authentication Flow - Access protected endpoint with invalid token")
    public void testProtectedEndpointWithInvalidToken() {
        given()
            .header("Authorization", "Bearer invalid-jwt-token")
            .when()
            .get("/auth/me")
            .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("Authentication Flow - CORS preflight request")
    public void testCorsPreflightRequest() {
        given()
            .header("Origin", "http://localhost:3000")
            .header("Access-Control-Request-Method", "POST")
            .header("Access-Control-Request-Headers", "Authorization, Content-Type")
            .when()
            .options("/auth/logout")
            .then()
            .statusCode(200)
            .header("Access-Control-Allow-Origin", "http://localhost:3000")
            .header("Access-Control-Allow-Methods", containsString("POST"));
    }
}
