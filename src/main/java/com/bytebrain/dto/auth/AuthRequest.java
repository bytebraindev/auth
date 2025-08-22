package com.bytebrain.dto.auth;

public class AuthRequest {
    
    public String code;
    public String state;
    public String error;
    public String errorDescription;
    
    // Constructors
    public AuthRequest() {}
    
    public AuthRequest(String code, String state) {
        this.code = code;
        this.state = state;
    }
}
