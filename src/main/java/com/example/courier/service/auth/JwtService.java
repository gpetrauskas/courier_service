package com.example.courier.service.auth;

import com.example.courier.dto.jwt.JwtClaims;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey secretKey = generateKey();

    public String createToken(String email, String role, String name) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(7200);
        String authToken = UUID.randomUUID().toString();
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("name", name)
                .claim("authToken", authToken)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public JwtClaims validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
            String subject = claims.getSubject();
            String role = claims.get("role", String.class);
            String name = claims.get("name", String.class);
            String authToken = claims.get("authToken", String.class);

            return new JwtClaims(subject, role, name, authToken);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT expired: {}", e.getMessage());
            throw new RuntimeException("JWT expired", e);
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        }
    }

    private SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSha256");
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptAuthToken(String authToken) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(authToken.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred encrypting auth token");
        }
    }

    public String decryptAuthToken(String encryptedAuthToken) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedAuthToken);
            byte[] decrypted = cipher.doFinal(decoded);

            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred decrypting auth token");
        }
    }
}
