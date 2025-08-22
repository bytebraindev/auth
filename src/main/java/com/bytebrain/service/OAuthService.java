package com.bytebrain.service;

import com.bytebrain.client.FacebookClient;
import com.bytebrain.client.GitHubClient;
import com.bytebrain.dto.auth.*;
import com.bytebrain.entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class OAuthService {
    
    @Inject
    @RestClient
    GitHubClient gitHubClient;
    
    @Inject
    @RestClient
    FacebookClient facebookClient;
    
    @Inject
    UserService userService;
    
    @Inject
    JwtService jwtService;
    
    @ConfigProperty(name = "github.client-id")
    String githubClientId;
    
    @ConfigProperty(name = "github.client-secret")
    String githubClientSecret;
    
    @ConfigProperty(name = "github.redirect-uri")
    String githubRedirectUri;
    
    @ConfigProperty(name = "facebook.client-id")
    String facebookClientId;
    
    @ConfigProperty(name = "facebook.client-secret")
    String facebookClientSecret;
    
    @ConfigProperty(name = "facebook.redirect-uri")
    String facebookRedirectUri;
    
    public AuthResponse authenticateWithGitHub(String code) {
        try {
            // Exchange code for access token
            OAuthTokenResponse tokenResponse = gitHubClient.getAccessToken(
                githubClientId, 
                githubClientSecret, 
                code, 
                githubRedirectUri
            );
            
            // Get user info from GitHub
            GitHubUserInfo userInfo = gitHubClient.getUserInfo("Bearer " + tokenResponse.accessToken);
            
            // Create or update user in our database
            User user = userService.createOrUpdateUser(
                userInfo.email,
                userInfo.name != null ? userInfo.name : userInfo.login,
                userInfo.avatarUrl,
                User.AuthProvider.GITHUB,
                userInfo.id.toString()
            );
            
            // Generate JWT
            String jwt = jwtService.generateToken(user);
            
            return new AuthResponse(jwt, user);
            
        } catch (Exception e) {
            throw new RuntimeException("GitHub authentication failed: " + e.getMessage(), e);
        }
    }
    
    public AuthResponse authenticateWithFacebook(String code) {
        try {
            // Exchange code for access token
            OAuthTokenResponse tokenResponse = facebookClient.getAccessToken(
                facebookClientId,
                facebookClientSecret,
                code,
                facebookRedirectUri
            );
            
            // Get user info from Facebook
            FacebookUserInfo userInfo = facebookClient.getUserInfo(
                tokenResponse.accessToken,
                "id,name,email,first_name,last_name,picture"
            );
            
            // Create or update user in our database
            String avatarUrl = userInfo.picture != null && userInfo.picture.data != null 
                ? userInfo.picture.data.url : null;
                
            User user = userService.createOrUpdateUser(
                userInfo.email,
                userInfo.name,
                avatarUrl,
                User.AuthProvider.FACEBOOK,
                userInfo.id
            );
            
            // Generate JWT
            String jwt = jwtService.generateToken(user);
            
            return new AuthResponse(jwt, user);
            
        } catch (Exception e) {
            throw new RuntimeException("Facebook authentication failed: " + e.getMessage(), e);
        }
    }
}
