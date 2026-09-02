package gytis.courier.adapter.out.jwt;

import gytis.courier.application.port.out.auth.JwtClaims;
import gytis.courier.application.port.out.auth.RefreshTokenResult;
import gytis.courier.application.port.out.auth.RefreshTokenValidationResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey key;
    private final long refreshExpiryMillis;
    private final long accessExpiryMillis;

    public JwtService(@Value("${jwt.secret}") String secretKey, @Value("${jwt.refresh.expiry}") long refreshExpiryMillis, @Value("${jwt.access.expiry}") long accessExpiryMillis) {
        byte[] bytes = secretKey.getBytes();
        key = Keys.hmacShaKeyFor(bytes);
        this.refreshExpiryMillis = refreshExpiryMillis;
        this.accessExpiryMillis = accessExpiryMillis;
    }

    public String createToken(Long id, String email, String role, String name) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessExpiryMillis);
        return Jwts.builder()
                .claim("id", id)
                .subject(email)
                .claim("role", role)
                .claim("name", name)
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(this.key)
                .compact();
    }

    public RefreshTokenResult createRefreshToken(Long id) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(refreshExpiryMillis);

        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .claim("id", id)
                .claim("type", "refresh")
                .claim("jti", jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(this.key)
                .compact();

        LocalDateTime createdAt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        LocalDateTime expiresAt = LocalDateTime.ofInstant(expiry, ZoneId.systemDefault());

        return new RefreshTokenResult(token, jti, createdAt, expiresAt);
    }

    public RefreshTokenValidationResult validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(this.key).build().parseSignedClaims(token).getPayload();
            String type = claims.get("type", String.class);
            if (!type.equals("refresh")) {
                throw new JwtException("Invalid JWT token");
            }
            return new RefreshTokenValidationResult(claims.get("id", Long.class), claims.get("jti", String.class));
        } catch (ExpiredJwtException e) {
            logger.warn("JWT refresh expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            logger.error("Invalid JWT refresh token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token", e);
        }
    }

    public JwtClaims validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(this.key).build().parseSignedClaims(token).getPayload();
            String type = claims.get("type", String.class);
            if (!type.equals("access")) {
                throw new JwtException("Invalid JWT token");
            }
            Long id = claims.get("id", Long.class);
            String subject = claims.getSubject();
            String role = claims.get("role", String.class);
            String name = claims.get("name", String.class);

            return new JwtClaims(id, subject, role, name);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token", e);
        }
    }
}
