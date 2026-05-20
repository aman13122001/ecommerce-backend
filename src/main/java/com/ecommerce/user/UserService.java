package com.ecommerce.user;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class UserService {

    public List<User> getAllUsers() {
        return User.listAll();
    }

    public User getUserById(Long id) {
        return User.findById(id);
    }

    public User getUserByEmail(String email) {
        return User.findByEmail(email);
    }

    @Transactional
    public User createUser(String email, String fullName, String password) {
        if (User.findByEmail(email) != null)
            throw new RuntimeException("Email already exists: " + email);
        User user = new User();
        user.email = email;
        user.fullName = fullName;
        user.passwordHash = BcryptUtil.bcryptHash(password);
        user.role = User.Role.CUSTOMER;
        user.persist();
        return user;
    }

    @Transactional
    public User updateUser(Long id, String fullName) {
        User user = User.findById(id);
        if (user == null) throw new RuntimeException("User not found: " + id);
        user.fullName = fullName;
        return user;
    }

    @Transactional
    public boolean deleteUser(Long id) {
        return User.deleteById(id);
    }
}
