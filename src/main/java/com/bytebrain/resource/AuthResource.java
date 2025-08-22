package com.bytebrain.resource;

import com.bytebrain.dto.auth.AuthResponse;
import com.bytebrain.service.GoogleOidcService;
import com.bytebrain.service.OAuthService;
import com.bytebrain.service.UserService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.net.URI;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    @Inject
    OAuthService oAuthService;
    
    @Inject
    GoogleOidcService googleOidcService;
    
    @Inject
    UserService userService;
    
    @Inject
    SecurityIdentity identity;
    
    @Inject
    JsonWebToken jwt;
    
    @ConfigProperty(name = "app.frontend.url")
    String frontendUrl;
    
    @ConfigProperty(name = "github.client-id")
    String githubClientId;
    
    @ConfigProperty(name = "facebook.client-id")
    String facebookClientId;
    
    // GitHub OAuth endpoints
    @GET
    @Path("/github")
    public Response initiateGitHubAuth(@QueryParam("state") String state) {
        String githubAuthUrl = String.format(
            "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=%s&state=%s",
            githubClientId,
            "http://localhost:8080/auth/github/callback",
            "user:email",
            state != null ? state : ""
        );
        
        return Response.temporaryRedirect(URI.create(githubAuthUrl)).build();
    }
    
    @GET
    @Path("/github/callback")
    public Response handleGitHubCallback(@QueryParam("code") String code, 
                                       @QueryParam("state") String state,
                                       @QueryParam("error") String error) {
        try {
            if (error != null) {
                return Response.temporaryRedirect(
                    URI.create(frontendUrl + "/auth/error?error=" + error)
                ).build();
            }
            
            if (code == null) {
                return Response.temporaryRedirect(
                    URI.create(frontendUrl + "/auth/error?error=missing_code")
                ).build();
            }
            
            AuthResponse authResponse = oAuthService.authenticateWithGitHub(code);
            
            // Redirect to frontend with token
            String redirectUrl = String.format("%s/auth/success?token=%s", 
                frontendUrl, authResponse.token);
            
            return Response.temporaryRedirect(URI.create(redirectUrl)).build();
            
        } catch (Exception e) {
            return Response.temporaryRedirect(
                URI.create(frontendUrl + "/auth/error?error=" + e.getMessage())
            ).build();
        }
    }
    
    // Facebook OAuth endpoints
    @GET
    @Path("/facebook")
    public Response initiateFacebookAuth(@QueryParam("state") String state) {
        String facebookAuthUrl = String.format(
            "https://www.facebook.com/v18.0/dialog/oauth?client_id=%s&redirect_uri=%s&scope=%s&state=%s",
            facebookClientId,
            "http://localhost:8080/auth/facebook/callback",
            "email,public_profile",
            state != null ? state : ""
        );
        
        return Response.temporaryRedirect(URI.create(facebookAuthUrl)).build();
    }
    
    @GET
    @Path("/facebook/callback")
    public Response handleFacebookCallback(@QueryParam("code") String code,
                                         @QueryParam("state") String state,
                                         @QueryParam("error") String error) {
        try {
            if (error != null) {
                return Response.temporaryRedirect(
                    URI.create(frontendUrl + "/auth/error?error=" + error)
                ).build();
            }
            
            if (code == null) {
                return Response.temporaryRedirect(
                    URI.create(frontendUrl + "/auth/error?error=missing_code")
                ).build();
            }
            
            AuthResponse authResponse = oAuthService.authenticateWithFacebook(code);
            
            // Redirect to frontend with token
            String redirectUrl = String.format("%s/auth/success?token=%s", 
                frontendUrl, authResponse.token);
            
            return Response.temporaryRedirect(URI.create(redirectUrl)).build();
            
        } catch (Exception e) {
            return Response.temporaryRedirect(
                URI.create(frontendUrl + "/auth/error?error=" + e.getMessage())
            ).build();
        }
    }
    
    // Google OIDC endpoint (alternative approach using direct token validation)
    @POST
    @Path("/google")
    public Response authenticateWithGoogle() {
        try {
            if (!identity.isAnonymous()) {
                AuthResponse authResponse = googleOidcService.authenticateWithGoogle(identity, jwt);
                return Response.ok(authResponse).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Google authentication failed\"}")
                    .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    // Get current user info (protected endpoint)
    @GET
    @Path("/me")
    @Authenticated
    public Response getCurrentUser() {
        try {
            String userId = jwt.getSubject();
            return userService.findById(Long.parseLong(userId))
                .map(user -> Response.ok(new AuthResponse.UserInfo(user)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    // Logout endpoint
    @POST
    @Path("/logout")
    @Authenticated
    public Response logout() {
        // In a stateless JWT setup, logout is typically handled client-side
        // by removing the token. You could implement token blacklisting here if needed.
        return Response.ok("{\"message\":\"Logged out successfully\"}").build();
    }
}
