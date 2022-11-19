package com.alexeykovzel.fi.features.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthService auth;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/register/admin")
    public void registerAdmin(@RequestBody Credentials credentials) {
        register(credentials, Authority.ADMIN);
    }

    @PostMapping("/register/guest")
    public void registerQuest(@RequestBody Credentials credentials) {
        register(credentials, Authority.QUEST);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public void deleteAccount() {
        userRepository.deleteByEmail(auth.getEmail());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void deleteAccountById(@PathVariable String id) {
        userRepository.deleteById(id);
    }

    private void register(Credentials credentials, Authority authority) {
        verifyCredentials(credentials);
        User user = buildUser(credentials, authority.single());
        userRepository.save(user);
        auth.login(credentials);
    }

    private User buildUser(Credentials credentials, List<Authority> authorities) {
        String email = credentials.getEmail();
        String password = encoder.encode(credentials.getPassword());
        return new User(email, password, authorities);
    }

    private void verifyCredentials(Credentials credentials) {
        String email = credentials.getEmail();
        String password = credentials.getPassword();

        if (isEmpty(email) || isEmpty(password))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing e-mail or password");

        if (userRepository.existsByEmail(email))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This e-mail is already taken");

        verifyRegex(password, ".{4,32}", "Password should be 4-64 characters long");
        verifyRegex(password, ".*[0-9].*", "Password should contain at least one digit");
        verifyRegex(password, ".*[a-z].*", "Password should contain at least one lowercase letter");
        verifyRegex(password, ".*[A-Z].*", "Password should contain at least one uppercase letter");
        verifyRegex(email, "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", "Invalid e-mail");
    }

    private void verifyRegex(String value, String regex, String error) {
        if (!value.matches(regex)) {
            log.error("Regex doesn't match: {}", error);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.equals("");
    }
}
