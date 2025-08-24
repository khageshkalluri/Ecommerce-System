package com.ecommerce.authservice.JWT;


import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final Key secret;
    public JwtService(@Value("${jwt.secret}") String secret) {
        byte[] bytes= Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
        this.secret= Keys.hmacShaKeyFor(bytes);
    }


    public String generateToken(String email,String role) {
    return Jwts.builder()
            .subject(email)
            .claim("role",role)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis()*1000*60*60*10))
            .signWith(secret)
            .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) secret)
                    .build().parseSignedClaims(token);
        }
       catch (SignatureException e){
            throw new JwtException("Invalid Jwt Signature");
       }
        catch (JwtException e){
            throw new JwtException("Invalid Jwt Token");
        }
    }
}
