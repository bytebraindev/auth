package com.bytebrain.service;

import com.bytebrain.entity.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class JwtService {
    
    @ConfigProperty(name = "app.jwt.expiration", defaultValue = "86400")
    long jwtExpiration;
    
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;
    
    public String generateToken(User user) {
        Set<String> groups = new HashSet<>();
        groups.add("user");
        
        return Jwt.issuer(issuer)
                .upn(user.email)
                .subject(user.id.toString())
                .claim("email", user.email)
                .claim("name", user.name)
                .claim("provider", user.provider.name())
                .claim("avatar_url", user.avatarUrl)
                .groups(groups)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtExpiration))
                .sign();
    }
    
    public boolean isTokenExpired(Instant expirationTime) {
        return expirationTime.isBefore(Instant.now());
    }
}
