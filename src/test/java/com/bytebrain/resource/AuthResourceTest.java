package com.bytebrain.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class AuthResourceTest {

    @Test
    public void testGitHubAuthInitiation() {
        given()
            .when()
            .get("/auth/github?state=test123")
            .then()
            .statusCode(307) // Temporary redirect
            .header("Location", containsString("github.com/login/oauth/authorize"));
    }

    @Test
    public void testFacebookAuthInitiation() {
        given()
            .when()
            .get("/auth/facebook?state=test123")
            .then()
            .statusCode(307) // Temporary redirect
            .header("Location", containsString("facebook.com"));
    }

    @Test
    public void testGitHubCallbackWithError() {
        given()
            .when()
            .get("/auth/github/callback?error=access_denied")
            .then()
            .statusCode(307) // Temporary redirect
            .header("Location", containsString("error=access_denied"));
    }

    @Test
    public void testFacebookCallbackWithError() {
        given()
            .when()
            .get("/auth/facebook/callback?error=access_denied")
            .then()
            .statusCode(307) // Temporary redirect
            .header("Location", containsString("error=access_denied"));
    }

    @Test
    public void testGetCurrentUserUnauthorized() {
        given()
            .when()
            .get("/auth/me")
            .then()
            .statusCode(401); // Unauthorized
    }

    @Test
    @TestSecurity(user = "testuser", roles = {"user"})
    public void testLogout() {
        given()
            .when()
            .post("/auth/logout")
            .then()
            .statusCode(200)
            .body(containsString("Logged out successfully"));
    }
}
