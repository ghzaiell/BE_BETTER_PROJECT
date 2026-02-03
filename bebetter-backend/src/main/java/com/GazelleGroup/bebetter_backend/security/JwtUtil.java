package com.GazelleGroup.bebetter_backend.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class JwtUtil {
    // === LOCAL JWT generation using HS512 ===
    private final String localSecret = "wHg82ZxFJlRBMzbW6pnN7ZnW2CH4OXYoyCHQMIoXIqaluY2iet4gidGJWg4v7q7XrBMV4kes6NBC0XdEedDUfw==";
    private final long expiration = 1000 * 60 * 60 * 10;
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, localSecret.getBytes())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(localSecret.getBytes())
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(localSecret.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


}
