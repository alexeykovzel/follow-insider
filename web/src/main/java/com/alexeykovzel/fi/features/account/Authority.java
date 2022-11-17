package com.alexeykovzel.fi.features.account;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public enum Authority implements GrantedAuthority {
    ADMIN,
    CUSTOMER,
    QUEST;

    public List<Authority> single() {
        return List.of(this);
    }

    @Override
    public String getAuthority() {
        return this.name();
    }
}
