package com.bytebrain.client;

import com.bytebrain.dto.auth.GitHubUserInfo;
import com.bytebrain.dto.auth.OAuthTokenResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "github-api")
public interface GitHubClient {
    
    @POST
    @Path("/login/oauth/access_token")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    OAuthTokenResponse getAccessToken(
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret,
            @FormParam("code") String code,
            @FormParam("redirect_uri") String redirectUri
    );
    
    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    GitHubUserInfo getUserInfo(@HeaderParam("Authorization") String authHeader);
}
