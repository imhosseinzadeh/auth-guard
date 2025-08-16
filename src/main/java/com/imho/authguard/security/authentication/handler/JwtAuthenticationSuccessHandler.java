package com.imho.authguard.security.authentication.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imho.authguard.domain.entity.user.User;
import com.imho.authguard.security.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();

        // Generate JWT tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Expiration time for access token
        String expiresAtIso = DateTimeFormatter.ISO_INSTANT.format(
                jwtUtil.decode(accessToken).getExpiresAt().toInstant()
        );

        // Create response
        Map<String, String> responseBody = Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken,
                "expires_at", expiresAtIso
        );

        writeJsonResponse(response, responseBody, HttpServletResponse.SC_OK);
    }

    private void writeJsonResponse(HttpServletResponse response, Map<String, String> responseBody, int status) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status);
        objectMapper.writeValue(response.getWriter(), responseBody);
    }

}
