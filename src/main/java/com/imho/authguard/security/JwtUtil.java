package com.imho.authguard.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.imho.authguard.domain.entity.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.token.expiry}")
    private long accessTokenExpirySeconds;

    @Value("${jwt.refresh.token.expiry}")
    private long refreshTokenExpirySeconds;

    /**
     * Generates access and refresh tokens for a user, along with expiration info.
     */
    public Map<String, String> generateTokens(User user) {
        // Generate JWT tokens
        String accessToken = this.generateAccessToken(user);
        String refreshToken = this.generateRefreshToken(user);

        // Expiration time for access token
        String expiresAtIso = DateTimeFormatter.ISO_INSTANT.format(
                this.decode(accessToken).getExpiresAt().toInstant()
        );

        return Map.of(
                "access_token", accessToken,
                "refresh_token", refreshToken,
                "expires_at", expiresAtIso
        );
    }

    /**
     * Generates an access token for the given user.
     */
    public String generateAccessToken(User user) {
        final Instant now = Instant.now();
        final Date issuedAt = Date.from(now);
        final Date expiresAt = Date.from(now.plusSeconds(accessTokenExpirySeconds));

        // Get user authorities
        List<String> authorities = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getUsername())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("authorities", authorities)
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * Generates a refresh token for the given user.
     */
    public String generateRefreshToken(User user) {
        final Instant now = Instant.now();
        final Date issuedAt = Date.from(now);
        final Date expiresAt = Date.from(now.plusSeconds(refreshTokenExpirySeconds));

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getUsername())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withJWTId(UUID.randomUUID().toString())
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * Extracts the username (subject) from a JWT without verifying it.
     */
    public String extractUsername(String token) {
        return JWT.decode(token).getSubject();
    }

    /**
     * Decodes and verifies a JWT token.
     *
     * @throws JWTDecodeException if token is invalid or verification fails
     */
    public DecodedJWT decode(final String token) throws JWTDecodeException {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token must not be null or empty");
        }

        return JWT.require(Algorithm.HMAC512(secret))
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

}
