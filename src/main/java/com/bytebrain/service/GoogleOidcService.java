package com.bytebrain.service;

import com.bytebrain.dto.auth.AuthResponse;
import com.bytebrain.entity.User;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class GoogleOidcService {
    
    @Inject
    UserService userService;
    
    @Inject
    JwtService jwtService;
    
    public AuthResponse authenticateWithGoogle(SecurityIdentity identity, JsonWebToken idToken) {
        try {
            // Extract user information from Google ID token
            String googleId = idToken.getSubject();
            String email = idToken.getClaim("email");
            String name = idToken.getClaim("name");
            String picture = idToken.getClaim("picture");
            Boolean emailVerified = idToken.getClaim("email_verified");
            
            if (!emailVerified) {
                throw new RuntimeException("Email not verified by Google");
            }
            
            // Create or update user in our database
            User user = userService.createOrUpdateUser(
                email,
                name,
                picture,
                User.AuthProvider.GOOGLE,
                googleId
            );
            
            // Generate our own JWT
            String jwt = jwtService.generateToken(user);
            
            return new AuthResponse(jwt, user);
            
        } catch (Exception e) {
            throw new RuntimeException("Google authentication failed: " + e.getMessage(), e);
        }
    }
}
