package com.imho.authguard.security.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imho.authguard.security.JwtUtil;
import com.imho.authguard.security.authentication.handler.JwtAuthenticationFailureHandler;
import com.imho.authguard.security.authentication.handler.JwtAuthenticationSuccessHandler;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final PathPatternRequestMatcher LOGIN_MATCHER = PathPatternRequestMatcher
            .withDefaults()
            .matcher(HttpMethod.POST, "/api/v1/auth/login");

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        super(LOGIN_MATCHER, authenticationManager);
        setAuthenticationConverter(new AuthenticationRequestConverter(objectMapper));
        setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler(objectMapper, jwtUtil));
        setAuthenticationFailureHandler(new JwtAuthenticationFailureHandler(objectMapper));
    }

}
