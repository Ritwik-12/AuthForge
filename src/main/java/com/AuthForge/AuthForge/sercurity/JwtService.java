package com.AuthForge.AuthForge.sercurity;


import com.AuthForge.AuthForge.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

        @Value("${spring.app.jwtAccessExpirationInMs}")
        private long jwtAccessExpirationInMs;

          @Value("${spring.app.jwtRefreshExpirationInMs}")
        private  long jwtRefreshExpirationInMs;
    //generateaccess token

    public String generateAccessToken(UUID userId, String email, Role role){



        return Jwts.builder()
                .subject(userId.toString())
                .claim("email",email)
                .claim("role",role.name())
                .claim("type","access")
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime()+jwtAccessExpirationInMs)))
                .signWith(key())
                .compact();
    }

    //generate referesh token

    public String generateRefreshToken(UUID userId){


        return Jwts.builder()
                .subject(userId.toString())
                .claim("type","refresh")
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime()+jwtRefreshExpirationInMs)))
                .signWith(key())
                .compact();
    }

    //parse claim

    public Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //extract user id

    public UUID extractUserId(String token){
        return UUID.fromString(parseClaims(token).getSubject());
    }

    //is access token
    public boolean isAccessToken(String token){
        return "access".equals(parseClaims(token).get("type",String.class));
    }

    public Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    public LocalDateTime getRefreshTokenExpiry(){
        return LocalDateTime.now().plusSeconds(refreshTokenExpiryDays*24*60*60);
    }
}
