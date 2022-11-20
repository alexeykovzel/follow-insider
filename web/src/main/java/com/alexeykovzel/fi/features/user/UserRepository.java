package com.alexeykovzel.fi.features.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User.Profile findProfileByEmail(String email);

    User findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
