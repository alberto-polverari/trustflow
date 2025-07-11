package it.trustflow.audit.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private String jwtSecret = "4261656C64756E674261656C64756E674261656C64756E67";

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            // Check if the token is expired
            Date expiration = extractClaims(token).getExpiration();
            return !expiration.before(new Date()); // Token is expired
        } catch (JwtException e) {
            return false;
        }
    }
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public Long getTenantId(String token) {
        return extractClaims(token).get("tenantId", Long.class);
    }
}
