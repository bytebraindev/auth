package com.bytebrain.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitHubUserInfo {
    
    public Long id;
    
    public String login;
    
    public String name;
    
    public String email;
    
    @JsonProperty("avatar_url")
    public String avatarUrl;
    
    @JsonProperty("html_url")
    public String htmlUrl;
    
    public String location;
    
    public String company;
}
