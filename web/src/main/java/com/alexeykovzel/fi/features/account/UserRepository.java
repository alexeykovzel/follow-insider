package com.alexeykovzel.fi.features.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
