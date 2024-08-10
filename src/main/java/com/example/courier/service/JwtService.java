package com.example.courier.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class JwtService {

    private SecretKey secretKey = generateKey();

    public String createToken(String email, String role, String name) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(1);
        String authToken = generateAuthToken();
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("name", name)
                .claim("authToken", authToken)
                .issuedAt(convertToDateViaInstant(now))
                .expiration(convertToDateViaInstant(expiry))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    private Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Map<String, String> validateToken(String token) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        String subject = claims.getSubject();
        String role = claims.get("role", String.class);
        String name = claims.get("name", String.class);
        String authToken = claims.get("authToken", String.class);

        Map<String, String> authDetails = new HashMap<>();
        authDetails.put("subject", subject);
        authDetails.put("role", role);
        authDetails.put("name", name);
        authDetails.put("authToken", authToken);

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
