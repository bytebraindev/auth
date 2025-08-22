package com.bytebrain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @Column(unique = true, nullable = false)
    public String email;

    @Column(nullable = false)
    public String name;

    @Column(name = "avatar_url")
    public String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public AuthProvider provider;

    @Column(name = "provider_id", nullable = false)
    public String providerId;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @Column(name = "last_login")
    public LocalDateTime lastLogin;

    @Column(nullable = false)
    public Boolean active = true;

    public enum AuthProvider {
        GOOGLE, GITHUB, FACEBOOK
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Static finder methods
    public static Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public static Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId) {
        return find("provider = ?1 and providerId = ?2", provider, providerId).firstResultOptional();
    }

    public static Optional<User> findByEmailAndProvider(String email, AuthProvider provider) {
        return find("email = ?1 and provider = ?2", email, provider).firstResultOptional();
    }
}
