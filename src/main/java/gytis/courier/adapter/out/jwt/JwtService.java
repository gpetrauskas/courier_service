package gytis.courier.adapter.out.jwt;

import gytis.courier.application.port.out.auth.JwtClaims;
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
import java.util.Date;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secretKey) {
        byte[] bytes = secretKey.getBytes();
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(Long id, String email, String role, String name) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(7200);
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

    public String createRefreshToken(Long id) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(240000);
        return Jwts.builder()
                .claim("id", id)
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(this.key)
                .compact();
    }

    public Long validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(this.key).build().parseSignedClaims(token).getPayload();
            String type = claims.get("type", String.class);
            if (!type.equals("refresh")) {
                throw new JwtException("Invalid JWT token");
            }
            return claims.get("id", Long.class);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT refresh expired: {}", e.getMessage());
            throw new RuntimeException("JWT expired", e);
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
            throw new RuntimeException("JWT expired", e);
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token", e);
        }
    }
}
