package com.bytebrain.integration;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class MockedOAuthFlowTest {

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up users before each test if needed
        // Note: User.deleteAll() requires proper Panache setup
    }

    @Test
    @DisplayName("GitHub OAuth Flow - Test Callback with Invalid Code")
    public void testGitHubOAuthInvalidCode() {
        // Test the callback with an invalid code that will fail
        given()
            .queryParam("code", "invalid_code")
            .queryParam("state", "test_state")
            .when()
            .get("/auth/github/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("auth/error"));
    }

    @Test
    @DisplayName("Facebook OAuth Flow - Test Callback with Invalid Code")
    public void testFacebookOAuthInvalidCode() {
        // Test the callback with an invalid code that will fail
        given()
            .queryParam("code", "invalid_code")
            .queryParam("state", "test_state")
            .when()
            .get("/auth/facebook/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("auth/error"));
    }

    @Test
    @DisplayName("OAuth Flow - Test State Parameter Validation")
    public void testOAuthStateValidation() {
        // Test GitHub without state parameter
        given()
            .when()
            .get("/auth/github")
            .then()
            .statusCode(307)
            .header("Location", containsString("github.com"));

        // Test Facebook without state parameter
        given()
            .when()
            .get("/auth/facebook")
            .then()
            .statusCode(307)
            .header("Location", containsString("facebook.com"));
    }

    @Test
    @DisplayName("OAuth Flow - Test Error Parameter Handling")
    public void testOAuthErrorParameterHandling() {
        // Test GitHub error parameter
        given()
            .queryParam("error", "access_denied")
            .queryParam("error_description", "The user denied the request")
            .when()
            .get("/auth/github/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("error=access_denied"));

        // Test Facebook error parameter
        given()
            .queryParam("error", "user_denied")
            .queryParam("error_description", "The user denied the request")
            .when()
            .get("/auth/facebook/callback")
            .then()
            .statusCode(307)
            .header("Location", containsString("error=user_denied"));
    }
}
