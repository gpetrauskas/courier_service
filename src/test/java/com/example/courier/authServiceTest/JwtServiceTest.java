package com.example.courier.authServiceTest;

import com.example.courier.dto.jwt.JwtClaims;
import com.example.courier.service.auth.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;

    private static final String TEST_NAME = "Test user";
    private static final String TEST_ROLE = "USER";
    private static final String TEST_EMAIL = "test@example.com";

    @Nested
    @DisplayName("success tests")
    class SuccessTests {
        @Test
        @DisplayName("create valid jwt with all claims")
        void createToken_returnsValidTokenWithCorrectClaims() {
            String token = jwtService.createToken(TEST_EMAIL, TEST_ROLE, TEST_NAME);

            assertNotNull(token);
            assertFalse(token.isEmpty());

            JwtClaims jwtClaims = jwtService.validateToken(token);
            assertEquals(TEST_EMAIL, jwtClaims.subject());
            assertEquals(TEST_NAME, jwtClaims.name());
            assertEquals(TEST_ROLE, jwtClaims.role());
        }

        @Test
        @DisplayName("should generate different authTokens for every jwt creation")
        void createToken_shouldGenerateUniqueAuthTokens() {
            String jwtOne = jwtService.createToken(TEST_EMAIL, TEST_ROLE, TEST_NAME);
            String jwtTwo = jwtService.createToken(TEST_EMAIL, TEST_ROLE, TEST_NAME);

            assertNotEquals(jwtOne, jwtTwo);

            JwtClaims firstClaims = jwtService.validateToken(jwtOne);
            JwtClaims secondClaims = jwtService.validateToken(jwtTwo);

            assertNotEquals(firstClaims.authToken(), secondClaims.authToken());
        }
    }

    @Nested
    @DisplayName("failure tests")
    class FailureTests {
        @Test
        @DisplayName("invalid jwt")
        void validateToken_rejectsBadJwt() {
            assertThrows(RuntimeException.class, () ->
                    jwtService.validateToken("bad"));
        }

        @Test
        @DisplayName("expired jwt")
        void validateToken_expired() throws NoSuchFieldException, IllegalAccessException {
            Field field = jwtService.getClass().getDeclaredField("secretKey");
            field.setAccessible(true);

            SecretKey secretKey = (SecretKey) field.get(jwtService);

            Instant expired = Instant.now().minus(1, ChronoUnit.HOURS);

            String expiredToken = Jwts.builder()
                    .subject(TEST_EMAIL)
                    .claim("role", TEST_ROLE)
                    .claim("name", TEST_NAME)
                    .claim("auth-token", "test-auth")
                    .issuedAt(Date.from(expired))
                    .expiration(Date.from(expired.plus(30, ChronoUnit.MINUTES)))
                    .signWith(secretKey)
                    .compact();

            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    jwtService.validateToken(expiredToken));

            assertEquals("JWT expired", exception.getMessage());
            assertInstanceOf(ExpiredJwtException.class, exception.getCause());
        }
    }

}
