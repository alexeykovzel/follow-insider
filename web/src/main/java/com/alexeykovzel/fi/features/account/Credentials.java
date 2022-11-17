package com.alexeykovzel.fi.features.account;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Data
public class Credentials {
    private final String email;
    private final String password;
}
