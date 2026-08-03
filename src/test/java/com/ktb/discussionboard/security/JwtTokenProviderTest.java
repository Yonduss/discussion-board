package com.ktb.discussionboard.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class JwtTokenProviderTest {

    private static final String SECRET =
            "test-only-jwt-secret-key-that-is-at-least-32-bytes-long";

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                SECRET,
                3_600_000L,
                1_209_600_000L,
                mock(UserDetailsService.class)
        );
    }

    @Test
    @DisplayName("Generated access token is accepted only as an access token")
    void accessToken_isAcceptedOnlyForAccessAuthentication() {
        String accessToken = jwtTokenProvider.generateAccessToken("test@test.com");

        assertThat(jwtTokenProvider.validateAccessToken(accessToken)).isTrue();
        assertThat(jwtTokenProvider.validateRefreshToken(accessToken)).isFalse();
        assertThat(jwtTokenProvider.getEmailFromToken(accessToken))
                .isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Generated refresh token is accepted only for token reissue")
    void refreshToken_isAcceptedOnlyForReissue() {
        String refreshToken = jwtTokenProvider.generateRefreshToken("test@test.com");

        assertThat(jwtTokenProvider.validateRefreshToken(refreshToken)).isTrue();
        assertThat(jwtTokenProvider.validateAccessToken(refreshToken)).isFalse();
        assertThat(jwtTokenProvider.getEmailFromToken(refreshToken))
                .isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("Legacy token without a type claim is rejected")
    void tokenWithoutType_isRejected() {
        Date now = new Date();
        String legacyToken = Jwts.builder()
                .subject("test@test.com")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 3_600_000L))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThat(jwtTokenProvider.validateAccessToken(legacyToken)).isFalse();
        assertThat(jwtTokenProvider.validateRefreshToken(legacyToken)).isFalse();
    }
}
