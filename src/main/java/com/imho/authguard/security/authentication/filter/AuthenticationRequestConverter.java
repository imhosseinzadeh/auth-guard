package com.imho.authguard.security.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationRequestConverter implements AuthenticationConverter {

    private final ObjectMapper mapper;

    @Override
    public UsernamePasswordAuthenticationToken convert(HttpServletRequest request) {
        try {
            AuthenticationRequest credentials = mapper.readValue(request.getInputStream(), AuthenticationRequest.class);

            return UsernamePasswordAuthenticationToken.unauthenticated(credentials.username(), credentials.password());
        } catch (IOException e) {
            log.error("Failed to parse authentication request", e);
            throw new AuthenticationServiceException("Invalid login request format", e);
        }
    }

}
