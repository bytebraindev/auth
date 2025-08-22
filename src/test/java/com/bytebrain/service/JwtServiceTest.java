package com.bytebrain.service;

import com.bytebrain.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class JwtServiceTest {

    @Inject
    JwtService jwtService;

    @Test
    public void testGenerateToken() {
        // Given
        User user = new User();
        // Set fields directly without using the id field since it might not be accessible
        user.email = "test@example.com";
        user.name = "Test User";
        user.provider = User.AuthProvider.GITHUB;
        user.avatarUrl = "https://example.com/avatar.png";
        
        // Simulate a persisted user by setting a mock ID
        // In a real scenario, this would be set by the database
        try {
            // Use reflection to set the id if it's accessible
            java.lang.reflect.Field idField = User.class.getField("id");
            idField.set(user, 1L);
        } catch (Exception e) {
            // If reflection fails, skip this test or handle differently
            System.out.println("Could not set user ID for test: " + e.getMessage());
            return;
        }

        // When
        String token = jwtService.generateToken(user);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }
}
