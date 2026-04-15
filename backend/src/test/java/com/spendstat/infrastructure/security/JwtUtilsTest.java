package com.spendstat.infrastructure.security;

import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.user.Email;
import com.spendstat.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    // Base64("ThisIsATestSecretKeyForSpendStat") — exactly 32 bytes, minimum for HS256
    private static final String TEST_SECRET = "VGhpc0lzQVRlc3RTZWNyZXRLZXlGb3JTcGVuZFN0YXQ=";

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "expiryMinutes", 15L);
    }

    @Test
    void generateAndExtract_roundTrip() {
        UserId userId = UserId.generate();
        User user = User.from(userId, Email.of("test@example.com"), "hash", Instant.now());

        String token = jwtUtils.generateAccessToken(user);
        Optional<UserId> extracted = jwtUtils.extractUserIdFromToken(token);

        assertThat(extracted).isPresent();
        assertThat(extracted.get()).isEqualTo(userId);
    }

    @Test
    void extractUserIdFromToken_returnsEmptyForGarbage() {
        Optional<UserId> result = jwtUtils.extractUserIdFromToken("not.a.jwt");
        assertThat(result).isEmpty();
    }

    @Test
    void extractUserIdFromToken_returnsEmptyForTamperedToken() {
        UserId userId = UserId.generate();
        User user = User.from(userId, Email.of("test@example.com"), "hash", Instant.now());
        String token = jwtUtils.generateAccessToken(user);

        // Tamper with the signature
        String tampered = token.substring(0, token.lastIndexOf('.') + 1) + "invalidsignature";
        Optional<UserId> result = jwtUtils.extractUserIdFromToken(tampered);

        assertThat(result).isEmpty();
    }
}
