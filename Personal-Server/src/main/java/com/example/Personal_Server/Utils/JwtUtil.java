package com.example.Personal_Server.Utils;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
    
    @Value("${security.jwt.secret}")
    private String secretKey;

    private final long ACCESS_TOKEN_EXPIRY = 15 * 60 * 1000;

    public String generateAccessToken(String userId, String deviceId, boolean biometricVerified) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("deviceId", deviceId)
                .claim("biometricVerified", biometricVerified) 
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Internal claims parser
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validate token (signature + expiry)
     */
    public boolean isTokenValid(String token, String userId, String deviceId) {
        try {
            Claims claims = getClaims(token);

            boolean userMatches = claims.getSubject().equals(userId);
            boolean deviceMatches = claims.get("deviceId", String.class).equals(deviceId);

            boolean notExpired = claims.getExpiration().after(new Date());
            return userMatches && deviceMatches && notExpired;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract username/userId from token
     */
    public String extractUserId(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extract deviceId from token (for device check in filter)
     */
    public String extractDeviceId(String token) {
        return getClaims(token).get("deviceId", String.class);
    }

    /**
     * Check that biometric flag is true for protected routes
     */
    public boolean isBiometricVerified(String token) {
        Boolean value = getClaims(token).get("biometricVerified", Boolean.class);
        return value != null && value;
    }
}
