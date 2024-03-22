package com.imho.authguard.authentication.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imho.authguard.authentication.EmailPasswordAuthenticationCredentials;
import com.imho.authguard.authentication.EmailPasswordAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class EmailPasswordAuthenticationConverter implements AuthenticationConverter {

    private final ObjectMapper mapper;

    @Override
    public EmailPasswordAuthenticationToken convert(HttpServletRequest request) {
        try {
            EmailPasswordAuthenticationCredentials credentials = mapper.readValue(request.getInputStream(), EmailPasswordAuthenticationCredentials.class);

            return EmailPasswordAuthenticationToken.unauthenticated(credentials.email(), credentials.password());
        } catch (IOException e) {
            throw new RuntimeException("Error reading request body", e);
        }
    }
}
