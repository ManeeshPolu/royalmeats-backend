package com.royalhalalmeats.royalmeats.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {

    // ✅ 256-bit key (generated once using Base64 encoder)
    // To generate your own key: see helper below
    private static final String SECRET =
            "vXx3T8GnSRKp8AaWUZ2p+ZJdX2+Q6wKNRqzCJ5E1M2E=";

    private static final Key KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));

    public static String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean validate(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public static String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Optional helper: generate new secret key (run once)
    public static void main(String[] args) {
        String newKey = Base64.getEncoder()
                .encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
        System.out.println("Generated JWT Secret Key:\n" + newKey);
    }
}
