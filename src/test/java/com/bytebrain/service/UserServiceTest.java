package com.bytebrain.service;

import com.bytebrain.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class UserServiceTest {

    @Inject
    UserService userService;

    @BeforeEach
    @Transactional
    void setUp() {
        // Clean up database before each test - manual cleanup if needed
        // Note: You might need to implement a cleanup method in your service
    }

    @Test
    public void testCreateNewUser() {
        // Given
        String email = "test@example.com";
        String name = "Test User";
        String avatarUrl = "https://example.com/avatar.png";
        User.AuthProvider provider = User.AuthProvider.GITHUB;
        String providerId = "12345";

        // When
        User user = userService.createOrUpdateUser(email, name, avatarUrl, provider, providerId);

        // Then
        assertNotNull(user);
        assertEquals(email, user.email);
        assertEquals(name, user.name);
        assertEquals(avatarUrl, user.avatarUrl);
        assertEquals(provider, user.provider);
        assertEquals(providerId, user.providerId);
        assertNotNull(user.createdAt);
        assertNotNull(user.updatedAt);
        assertNotNull(user.lastLogin);
        assertTrue(user.active);
    }

    @Test
    public void testUpdateExistingUser() {
        // Given - create initial user
        String email = "test@example.com";
        String initialName = "Initial Name";
        String updatedName = "Updated Name";
        String avatarUrl = "https://example.com/avatar.png";
        String updatedAvatarUrl = "https://example.com/new-avatar.png";
        User.AuthProvider provider = User.AuthProvider.GOOGLE;
        String providerId = "google123";

        userService.createOrUpdateUser(email, initialName, avatarUrl, provider, providerId);

        // When - update the same user
        User updatedUser = userService.createOrUpdateUser(email, updatedName, updatedAvatarUrl, provider, providerId);

        // Then
        assertEquals(updatedName, updatedUser.name);
        assertEquals(updatedAvatarUrl, updatedUser.avatarUrl);
        assertEquals(email, updatedUser.email);
        assertEquals(provider, updatedUser.provider);
        assertEquals(providerId, updatedUser.providerId);
    }

    @Test
    public void testFindUserByEmail() {
        // Given
        String email = "test@example.com";
        User user = userService.createOrUpdateUser(
            email, 
            "Test User", 
            "avatar.png", 
            User.AuthProvider.GITHUB, 
            "gh123"
        );

        // When
        Optional<User> foundUser = userService.findByEmail(email);

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(user.email, foundUser.get().email);
    }

    @Test
    public void testFindUserByEmailNotFound() {
        // When
        Optional<User> foundUser = userService.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(foundUser.isPresent());
    }
}
