package com.imho.authguard.security.authorization;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.imho.authguard.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER_STRING = "Authorization";
    private static final String TOKEN_BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER_STRING);
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.replace(TOKEN_BEARER_PREFIX, "");
        DecodedJWT decodedJWT;
        try {
            decodedJWT = jwtUtil.decode(token);
        } catch (JWTVerificationException e) {
            // Log and continue without setting auth (reject or anonymous)
            log.warn("Invalid JWT token: {}", e.getMessage());

            filterChain.doFilter(request, response);
            return;
        }

        String username = decodedJWT.getSubject();

        List<SimpleGrantedAuthority> authorities = decodedJWT
                .getClaim("authorities")
                .asList(SimpleGrantedAuthority.class);

        UsernamePasswordAuthenticationToken authenticationToken = UsernamePasswordAuthenticationToken.authenticated(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

}
