package com.imho.authguard.security.authentication.filter;

import org.springframework.security.authentication.BadCredentialsException;

public record AuthenticationRequest(String username, String password) {

    public AuthenticationRequest {
        if (username == null || username.isBlank()) {
            throw new BadCredentialsException("Username cannot be null or empty");
        }
        if (password == null || password.isBlank()) {
            throw new BadCredentialsException("Password cannot be null or empty");
        }
    }

}
