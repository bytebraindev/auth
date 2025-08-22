package com.bytebrain.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookUserInfo {
    
    public String id;
    
    public String name;
    
    public String email;
    
    @JsonProperty("first_name")
    public String firstName;
    
    @JsonProperty("last_name")
    public String lastName;
    
    public Picture picture;
    
    public static class Picture {
        public Data data;
        
        public static class Data {
            public String url;
            public Integer width;
            public Integer height;
        }
    }
}
