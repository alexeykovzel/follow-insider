package com.alexeykovzel.fi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.requiresChannel().anyRequest().requiresSecure();   // redirect to https
        http.authorizeRequests().anyRequest().permitAll();      // allow any requests
        http.httpBasic().disable();                             // disable default login
        return http.build();
    }
}