package com.imho.authguard.authentication;

import org.springframework.security.authentication.BadCredentialsException;

public record EmailPasswordAuthenticationCredentials(String email, String password) {

    public EmailPasswordAuthenticationCredentials {
        if (email == null || email.isBlank()) {
            throw new BadCredentialsException("Email cannot be null or empty");
        }
        if (password == null || password.isBlank()) {
            throw new BadCredentialsException("Password cannot be null or empty");
        }
    }
}
