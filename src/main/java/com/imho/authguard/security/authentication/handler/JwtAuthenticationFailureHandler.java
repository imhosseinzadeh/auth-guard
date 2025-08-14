package com.imho.authguard.security.authentication.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn("Authentication failed: {}", exception.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        if (exception instanceof UsernameNotFoundException || exception instanceof BadCredentialsException)
            errorResponse.put("error", "Incorrect username or password. Please check your credentials and try again.");
        else if (exception instanceof AuthenticationServiceException) {
            errorResponse.put("error", "Username and password must not be empty.");
        } else
            errorResponse.put("error", "Authentication failed. If the issue persists, please contact support for assistance.");

        writeJsonResponse(response, errorResponse, HttpStatus.UNAUTHORIZED.value());
    }

    private void writeJsonResponse(HttpServletResponse response, Map<String, String> responseBody, int status) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status);
        objectMapper.writeValue(response.getWriter(), responseBody);
    }

}
