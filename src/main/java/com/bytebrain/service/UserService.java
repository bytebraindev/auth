package com.bytebrain.service;

import com.bytebrain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class UserService {
    
    @Transactional
    public User createOrUpdateUser(String email, String name, String avatarUrl, 
                                  User.AuthProvider provider, String providerId) {
        Optional<User> existingUser = User.findByProviderAndProviderId(provider, providerId);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update user information
            user.name = name;
            user.avatarUrl = avatarUrl;
            user.lastLogin = LocalDateTime.now();
            user.persist();
            return user;
        }
        
        // Check if user exists with same email but different provider
        Optional<User> userByEmail = User.findByEmail(email);
        if (userByEmail.isPresent()) {
            // For this example, we'll create a new user record
            // In production, you might want to link accounts
        }
        
        // Create new user
        User newUser = new User();
        newUser.email = email;
        newUser.name = name;
        newUser.avatarUrl = avatarUrl;
        newUser.provider = provider;
        newUser.providerId = providerId;
        newUser.lastLogin = LocalDateTime.now();
        newUser.persist();
        
        return newUser;
    }
    
    public Optional<User> findById(Long id) {
        return User.findByIdOptional(id);
    }
    
    public Optional<User> findByEmail(String email) {
        return User.findByEmail(email);
    }
    
    @Transactional
    public void updateLastLogin(Long userId) {
        User user = User.findById(userId);
        if (user != null) {
            user.lastLogin = LocalDateTime.now();
            user.persist();
        }
    }
}
