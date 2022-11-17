package com.alexeykovzel.fi.features.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @PostMapping("/signup")
    public void signUpQuest(MultiValueMap<String, String> data) {
        signUp(getCredentials(data), Authority.QUEST);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/signup/admin")
    public void signUpAdmin(MultiValueMap<String, String> data) {
        signUp(getCredentials(data), Authority.ADMIN);
    }

    private void signUp(Credentials credentials, Authority authority) {
        verifyCredentials(credentials);
        User user = buildUser(credentials, authority.single());
        userRepository.save(user);
    }

    private User buildUser(Credentials credentials, List<Authority> authorities) {
        String email = credentials.getEmail();
        String password = encoder.encode(credentials.getPassword());
        return new User(email, password, authorities);
    }

    private Credentials getCredentials(MultiValueMap<String, String> data) {
        String username = getField(data, USERNAME_FIELD);
        String password = getField(data, PASSWORD_FIELD);
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
        if (!value.matches(regex))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
    }

    private String getField(MultiValueMap<String, String> data, String name) {
        List<String> elements = data.get(name);
        return (elements == null || elements.isEmpty()) ? null : elements.get(0);
    }

    private boolean isEmpty(String value) {
        return value == null || value.equals("");
    }
}
