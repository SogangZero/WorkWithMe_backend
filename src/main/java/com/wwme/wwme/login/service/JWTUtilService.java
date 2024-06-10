package com.wwme.wwme.login.service;

import com.wwme.wwme.login.exception.JwtTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtilService {
    private SecretKey secretKey;

    public JWTUtilService(@Value("${spring.jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUserKey(String token) throws JwtTokenException {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("userKey", String.class);
        } catch (Exception e) {
            throw new JwtTokenException("Cannot Find UserKey In the Token");
        }
    }

    public String getRole(String token) throws JwtTokenException {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
        } catch (Exception e) {
            throw new JwtTokenException("Cannot Find Role In the Token");
        }
    }

    public String getCategory(String token) throws JwtTokenException {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
        } catch (Exception e) {
            throw new JwtTokenException("Cannot Find Category In the Token");
        }
    }

    public Boolean isExpired(String token) throws JwtTokenException{
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            throw new JwtTokenException("Token Is Expired");
        }
    }

    public String createJwt(String category, String userKey, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("userKey", userKey)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

}

