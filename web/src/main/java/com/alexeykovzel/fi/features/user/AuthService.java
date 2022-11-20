package com.alexeykovzel.fi.features.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthProvider provider;

    public String getEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    public boolean hasAuthority(Authority authority) {
        return getAuthorities().contains(authority);
    }

    public void login(Credentials credentials) throws ResponseStatusException {
        var token = new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
        try {
            SecurityContextHolder.getContext().setAuthentication(provider.authenticate(token));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
