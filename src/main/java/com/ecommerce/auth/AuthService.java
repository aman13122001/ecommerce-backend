package com.ecommerce.auth;

import com.ecommerce.user.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    public String generateToken(User user) {
        return Jwt.issuer("ecommerce-backend")
                .upn(user.email)
                .subject(String.valueOf(user.id))
                .groups(Set.of(user.role.name()))
                .expiresIn(Duration.ofHours(2))
                .sign();
    }
}
