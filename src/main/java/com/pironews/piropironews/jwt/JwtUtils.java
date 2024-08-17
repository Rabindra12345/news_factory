package com.pironews.piropironews.jwt;


import com.pironews.piropironews.model.User;
import com.pironews.piropironews.service.UserDetailsImpl;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${baldur.app.jwtSecret}")
    private String jwtSecret;

    @Value("${baldur.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(User authentication) {

//        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        LocalDateTime tokenExpirationTime = LocalDateTime.now().plusSeconds(120);
//        Date currentTimeInDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());

        Date tokenExpirationTimeInDate = Date.from(tokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject((authentication.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(tokenExpirationTimeInDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public String generateJwtTokenWithUserInfo(User user) {

//        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        LocalDateTime tokenExpirationTime = LocalDateTime.now().plusSeconds(120);
//        Date currentTimeInDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());

        Date tokenExpirationTimeInDate = Date.from(tokenExpirationTime.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject((user.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(tokenExpirationTimeInDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
}
