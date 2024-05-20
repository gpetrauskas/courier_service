package com.example.courier.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private SecretKey secretKey = generateKey();

    public String createToken(String email, String role) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(1);
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(convertToDateViaInstant(now))
                .expiration(convertToDateViaInstant(expiry))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Map<String, String> validateToken(String token) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        String subject = claims.getSubject();
        String role = claims.get("role", String.class);

        Map<String, String> authDetails = new HashMap<>();
        authDetails.put("subject", subject);
        authDetails.put("role", role);

        return authDetails;
    }

    private SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSha256");
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
