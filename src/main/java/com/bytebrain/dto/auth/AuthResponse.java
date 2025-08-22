package com.bytebrain.dto.auth;

import com.bytebrain.entity.User;

public class AuthResponse {
    
    public String token;
    public UserInfo user;
    
    public AuthResponse() {}
    
    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = new UserInfo(user);
    }
    
    public static class UserInfo {
        public Long id;
        public String email;
        public String name;
        public String avatarUrl;
        public String provider;
        
        public UserInfo() {}
        
        public UserInfo(User user) {
            this.id = user.id;
            this.email = user.email;
            this.name = user.name;
            this.avatarUrl = user.avatarUrl;
            this.provider = user.provider.name();
        }
    }
}
