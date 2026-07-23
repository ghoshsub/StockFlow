package com.stockflow.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService — The JWT engine of the application.
 *
 * Responsibilities:
 *  1. Generate signed JWT tokens using HMAC-SHA256 (HS256).
 *  2. Extract claims (subject, expiry, custom fields) from tokens.
 *  3. Validate tokens against UserDetails (signature + expiry + username match).
 *
 * Algorithm used : HS256 (HMAC + SHA-256) — symmetric key, secret stored in application.yml.
 * Library        : io.jsonwebtoken (JJWT) 0.12.5
 *
 * Token anatomy:
 *  Header  → {"alg": "HS256", "typ": "JWT"}
 *  Payload → {"sub": "username", "role": "ADMIN", "iat": ..., "exp": ...}
 *  Signature → HMAC_SHA256(base64(header) + "." + base64(payload), secret)
 */
@Service
public class JwtService {

    @Value("${jwt.secret:5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    // ── Token Generation ─────────────────────────────────────────────────────

    /**
     * Generate a token with no extra claims (uses username as subject).
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generate a token embedding extra claims (e.g. role) into the payload.
     *
     * @param extraClaims  key-value pairs to embed in the JWT payload
     * @param userDetails  the authenticated user (username becomes the "sub" claim)
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)                           // custom claims first
                .subject(userDetails.getUsername())            // "sub" claim = username
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())                     // HS256 signing
                .compact();
    }

    // ── Token Validation ─────────────────────────────────────────────────────

    /**
     * Validate a token against a UserDetails object.
     * A token is valid only if:
     *   (a) the username embedded in it matches the UserDetails username, AND
     *   (b) the token has not yet expired.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ── Claims Extraction ────────────────────────────────────────────────────

    /** Extract the "sub" (subject = username) claim. */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Generic claim extractor using a function reference. */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ── Private Helpers ──────────────────────────────────────────────────────

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Parse and return all claims from the token.
     * Throws JwtException (subclasses) if the token is tampered or expired.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())    // verify signature
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Decode the Base64-encoded secret from config and build an HMAC-SHA key.
     * JJWT 0.12.x requires at least 256 bits for HS256.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
