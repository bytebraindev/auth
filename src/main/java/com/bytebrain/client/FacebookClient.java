package com.bytebrain.client;

import com.bytebrain.dto.auth.FacebookUserInfo;
import com.bytebrain.dto.auth.OAuthTokenResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "facebook-api")
public interface FacebookClient {
    
    @POST
    @Path("/oauth/access_token")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    OAuthTokenResponse getAccessToken(
            @FormParam("client_id") String clientId,
            @FormParam("client_secret") String clientSecret,
            @FormParam("code") String code,
            @FormParam("redirect_uri") String redirectUri
    );
    
    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    FacebookUserInfo getUserInfo(
            @QueryParam("access_token") String accessToken,
            @QueryParam("fields") String fields
    );
}
