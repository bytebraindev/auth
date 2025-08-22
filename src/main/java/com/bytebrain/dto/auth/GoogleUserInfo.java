package com.bytebrain.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleUserInfo {
    
    @JsonProperty("sub")
    public String id;
    
    public String email;
    
    @JsonProperty("email_verified")
    public Boolean emailVerified;
    
    public String name;
    
    @JsonProperty("given_name")
    public String givenName;
    
    @JsonProperty("family_name")
    public String familyName;
    
    public String picture;
    
    public String locale;
}
