package com.ecommerce.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true)
    public String email;

    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    @Column(name = "full_name")
    public String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role role = Role.CUSTOMER;

    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();

    public enum Role { CUSTOMER, ADMIN }

    public static User findByEmail(String email) {
        return find("email", email).firstResult();
    }
}
