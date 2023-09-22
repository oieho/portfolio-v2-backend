package com.oieho.entity;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthToken {
	
	@Getter
    private String token;
    private final Key key;
    
	@Value("${jwt.refreshExpire}")
	private long refreshExpire; // 864000000 == 10days
    
    @Override
    public String toString() {
    	return token;
    }
    
    public AuthToken(String id, Key key) {
        this.key = key;
        this.token = createAuthToken(id, refreshExpire);
    }
    
    public AuthToken(long userNo, String userId, String refreshToken, List<String> roles, long refreshExpire, Key key) {
        this.key = key;
        this.token = createAuthToken(userNo, userId, refreshToken, roles, refreshExpire);
    }
    
    private String createAuthToken(String id, long refreshExpire) {
        return Jwts.builder()
                .setSubject(id)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpire))
                .compact();
    }
    
    private String createAuthToken(long userNo, String userId, String refreshToken, List<String> roles, long refreshExpire) {
        return Jwts.builder()
                .setSubject(refreshToken)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpire))
    			.claim("uno", userNo)
    			.claim("uid", userId)
    			.claim("rol", roles)
                .compact();
    }
    
    public boolean validate() {
        return this.getTokenClaims() == null;
    }
    
    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null; // 토큰이 유효하면 null반환
    }
    
    public Claims getExpiredTokenClaims(String oldAccessToken) {
        try {
        	return Optional.ofNullable(Jwts.parserBuilder()
                     .setSigningKey(key)
                     .build()
                     .parseClaimsJws(oldAccessToken)
                     .getBody()).orElseThrow(()->null);
         } catch (ExpiredJwtException e) {
             log.info("Expired JWT token.");
             return null;
         }
    }

}
