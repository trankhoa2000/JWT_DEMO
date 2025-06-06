package org.example.jwtdemo.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;
import org.example.jwtdemo.security.userprincal.UserPrinciple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpiration = 86400;

    private Key key;

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            logger.error("JWT secret is not configured properly");
            throw new IllegalStateException("JWT secret key is not configured");
        }
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String createToken(Authentication authentication){
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        return Jwts.builder()
                   .setSubject(userPrinciple.getUsername())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000L))
                   .signWith(key, SignatureAlgorithm.HS512)
                   .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e){
            logger.error("Invalid JWT signature -> Message: {}", e);
        } catch (MalformedJwtException e){
            logger.error("Invalid Token format -> Message: {}",e);
        } catch (UnsupportedJwtException e){
            logger.error("Unsupported JWT token -> Message: {}",e);
        } catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty -Message {}",e);
        } catch (ExpiredJwtException e){
            logger.error("JWT Token is expired -> Message: {}",e);
        }
        return false;
    }

    public String getUserNameFromToken(String token){
        return Jwts.parser()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }
}
