package gytis.courier;

import gytis.courier.adapter.out.jwt.JwtService;
import gytis.courier.application.port.out.auth.JwtClaims;
import gytis.courier.application.port.out.auth.RefreshTokenResult;
import gytis.courier.application.port.out.auth.RefreshTokenValidationResult;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {
    private final JwtService jwtService = new JwtService("secretNotReallyRandomAlsoNotKey123456789justForTesting123456789", 9999, 99);
    private final JwtService jwtServiceTwo = new JwtService("SECRETNotReallyRandomAlsoNotKey123456789justForTesting123456789", 9999, 99);
    private final JwtService serviceShortExpiry = new JwtService("SECRETNotReallyRandomAlsoNotKey123456789justForTesting123456789", 1, 1);

    private final String accessToken = jwtService.createToken(1L, "me@example.com", "USER", "me");
    private final RefreshTokenResult refreshToken = jwtService.createRefreshToken(1L);

    @Test
    void successfullyCreateToken() {
        RefreshTokenResult refreshToken = jwtService.createRefreshToken(1L);

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
        RefreshTokenValidationResult result = jwtService.validateRefreshToken(refreshToken.token());

        assertEquals(1L, result.personId());
    }

    @Test
    void throwOnValidateRefreshWrongTokenType() {
        assertThrows(JwtException.class, () -> jwtService.validateRefreshToken(accessToken));
    }

    @Test
    void throwOnValidateAccessWrongTokenType() {
        assertThrows(JwtException.class, () -> jwtService.validateToken(refreshToken.token()));
    }

    @Test
    void throwOnExpiredJwtToken() throws InterruptedException {
        String jwtToken = serviceShortExpiry.createToken(1L, "me@example.com", "USER", "me");
        Thread.sleep(1100);
        assertThrows(ExpiredJwtException.class, () -> serviceShortExpiry.validateToken(jwtToken));
    }

    @Test
    void throwOnExpiredJwtRefreshToken() throws InterruptedException {
        RefreshTokenResult refreshTkn = serviceShortExpiry.createRefreshToken(1L);
        Thread.sleep(1100);
        assertThrows(ExpiredJwtException.class, () -> serviceShortExpiry.validateToken(refreshTkn.token()));
    }

    @Test
    void throwOnWrongSecretKeyValidateAccess() {
        String serviceTwoAccessToken = jwtServiceTwo.createToken(1L, "me@example.com", "USER", "me");
        assertThrows(JwtException.class, () -> jwtService.validateToken(serviceTwoAccessToken));
    }

    @Test
    void throwOnWrongSecretKeyValidateRefresh() {
        RefreshTokenResult serviceTwoRefreshToken = jwtServiceTwo.createRefreshToken(1L);
        assertThrows(JwtException.class, () -> jwtService.validateRefreshToken(serviceTwoRefreshToken.token()));
    }





}
