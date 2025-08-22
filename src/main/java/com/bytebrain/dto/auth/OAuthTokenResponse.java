package com.bytebrain.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthTokenResponse {
    
    @JsonProperty("access_token")
    public String accessToken;
    
    @JsonProperty("token_type")
    public String tokenType;
    
    @JsonProperty("expires_in")
    public Integer expiresIn;
    
    @JsonProperty("refresh_token")
    public String refreshToken;
    
    public String scope;
}
