package com.imho.authguard.authentication.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.imho.authguard.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${token.access.expire:3600}") // 1 hour
    private long accessTokenExpireTime;

    @Value("${token.refresh.expire:86400}") // 24 hours
    private long refreshTokenExpireTime;

    @Value("${token.secret:asecrectpasswordfortoken}")
    private String secret;

    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpireTime);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpireTime);
    }

    private String generateToken(User user, long expireTime) {
        List<String> authorities = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiresAt = Date.from(now.plusSeconds(expireTime));

        return JWT.create()
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withSubject(user.getUsername())
                .withClaim("authorities", authorities)
                .sign(Algorithm.HMAC512(secret));
    }

    public DecodedJWT decode(String token) throws JWTDecodeException {
        return JWT.require(Algorithm.HMAC512(secret))
                .build()
                .verify(token);
    }

}
