package gytis.courier;

import gytis.courier.adapter.out.jwt.JwtService;
import gytis.courier.application.port.out.auth.JwtClaims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {
    private final JwtService jwtService = new JwtService("secretNotReallyRandomAlsoNotKey123456789justForTesting123456789");
    private final JwtService jwtServiceTwo = new JwtService("SECRETNotReallyRandomAlsoNotKey123456789justForTesting123456789");

    private final String accessToken = jwtService.createToken(1L, "me@example.com", "USER", "me");
    private final String refreshToken = jwtService.createRefreshToken(1L);

    @Test
    void successfullyCreateToken() {
        String refreshToken = jwtService.createRefreshToken(1L);

        assertNotNull(accessToken);
        assertNotNull(refreshToken);
    }

    @Test
    void successValidateAccessToken() {
        JwtClaims claims = jwtService.validateToken(accessToken);

        assertNotNull(claims);
        assertEquals(1L, claims.id());
        assertEquals("me@example.com", claims.subject());
    }

    @Test
    void successValidateRefreshToken() {
        Long userId = jwtService.validateRefreshToken(refreshToken);

        assertEquals(1L, userId);
    }

    @Test
    void throwOnValidateRefreshWrongTokenType() {
        assertThrows(JwtException.class, () -> jwtService.validateRefreshToken(accessToken));
    }

    @Test
    void throwOnValidateAccessWrongTokenType() {
        assertThrows(JwtException.class, () -> jwtService.validateToken(refreshToken));
    }

    @Test
    void throwOnWrongSecretKeyValidateAccess() {
        String serviceTwoAccessToken = jwtServiceTwo.createToken(1L, "me@example.com", "USER", "me");
        assertThrows(JwtException.class, () -> jwtService.validateToken(serviceTwoAccessToken));
    }

    @Test
    void throwOnWrongSecretKeyValidateRefresh() {
        String serviceTwoRefreshToken = jwtServiceTwo.createRefreshToken(1L);
        assertThrows(JwtException.class, () -> jwtService.validateRefreshToken(serviceTwoRefreshToken));
    }





}
