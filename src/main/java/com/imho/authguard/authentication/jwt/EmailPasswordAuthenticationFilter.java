package com.imho.authguard.authentication.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imho.authguard.authentication.EmailPasswordAuthenticationToken;
import com.imho.authguard.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EmailPasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final AntPathRequestMatcher LOGIN_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/v1/users/login", "POST");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmailPasswordAuthenticationConverter authenticationConverter = new EmailPasswordAuthenticationConverter(objectMapper);

    private final JwtUtil jwtUtil;

    public EmailPasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(LOGIN_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        EmailPasswordAuthenticationToken authentication = authenticationConverter.convert(request);
        if (authentication == null) {
            return null;
        }

        return super.getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();

        // Generate JWT tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Expiration time for access token
        Date expiration = jwtUtil.decode(accessToken).getExpiresAt();

        // create response
        Map<String, String> responseBody = Map.of(
                "token", accessToken,
                "refreshToken", refreshToken,
                "expiration", expiration.toString()
        );

        writeJsonResponse(response, responseBody, HttpServletResponse.SC_OK);

        super.successfulAuthentication(request, response, chain, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        Map<String, String> errorResponse = new HashMap<>();
        if (failed instanceof UsernameNotFoundException || failed instanceof BadCredentialsException)
            errorResponse.put("error", "Incorrect email address or password. Please check your credentials and try again.");
        else
            errorResponse.put("error", "Authentication failed. If the issue persists, please contact support for assistance.");

        writeJsonResponse(response, errorResponse, HttpStatus.UNAUTHORIZED.value());

        super.unsuccessfulAuthentication(request, response, failed);
    }

    private void writeJsonResponse(HttpServletResponse response, Map<String, String> responseBody, int status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        objectMapper.writeValue(response.getWriter(), responseBody);
    }
}
