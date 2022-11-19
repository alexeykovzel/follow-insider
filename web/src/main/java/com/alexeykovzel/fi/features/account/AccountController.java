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
    public void registerAdmin(@RequestBody MultiValueMap<String, String> data) {
        register(data, Authority.ADMIN);
    }

    @PostMapping("/register/guest")
    public void registerQuest(@RequestBody MultiValueMap<String, String> data) {
        register(data, Authority.QUEST);
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

    private void register(MultiValueMap<String, String> data, Authority authority) {
        Credentials credentials = getCredentials(data);
        verifyCredentials(credentials);
        User user = User.builder()
                .fullname(getField(data, "fullname"))
                .email(credentials.getEmail())
                .password(encoder.encode(credentials.getPassword()))
                .authorities(authority.single())
                .build();
        userRepository.save(user);
        auth.login(credentials);
    }

    private Credentials getCredentials(MultiValueMap<String, String> data) {
        String username = getField(data, "email");
        String password = getField(data, "password");
        return new Credentials(username, password);
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

    private String getField(MultiValueMap<String, String> data, String name) {
        List<String> elements = data.get(name);
        return (elements == null || elements.isEmpty()) ? null : elements.get(0);
    }

    private boolean isEmpty(String value) {
        return value == null || value.equals("");
    }
}
