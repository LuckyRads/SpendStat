package com.spendstat.infrastructure.security;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.user.User;
import com.spendstat.domain.user.port.out.TokenGeneratorPort;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtUtils implements TokenGeneratorPort {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiry-minutes}")
    private long expiryMinutes;

    @Override
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().getValue().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMinutes * 60_000L))
                .signWith(signingKey())
                .compact();
    }

    @Override
    public Optional<UserId> extractUserIdFromToken(String token) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Optional.of(UserId.of(UUID.fromString(subject)));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
