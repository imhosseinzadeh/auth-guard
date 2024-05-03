package com.imho.authguard.authorization;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.imho.authguard.authentication.EmailPasswordAuthenticationToken;
import com.imho.authguard.authentication.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

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
        DecodedJWT decodedJWT = jwtUtil.decode(token);

        String email = decodedJWT.getSubject();

        Claim authoritiesClaim = decodedJWT.getClaim("authorities");
        List<SimpleGrantedAuthority> authorities = authoritiesClaim.asList(SimpleGrantedAuthority.class);

        EmailPasswordAuthenticationToken authenticationToken = EmailPasswordAuthenticationToken.authenticated(email, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

}
