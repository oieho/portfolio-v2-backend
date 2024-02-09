package com.oieho.jwt;
import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class JwtConfig {
    private String signingKey;
    private String authToken;
    private long accessExpire;
    private long refreshExpire;
    private Key hmacShaKey;

    @Autowired
    public void init(@Value("${jwt.signingKey}") String signingKey,
                     @Value("${jwt.authToken}") String authToken,
                     @Value("${jwt.accessExpire}") long accessExpire,
                     @Value("${jwt.refreshExpire}") long refreshExpire) {
        this.signingKey = signingKey;
        this.authToken = authToken;
        this.accessExpire = accessExpire;
        this.refreshExpire = refreshExpire;
        this.hmacShaKey = Keys.hmacShaKeyFor(signingKey.getBytes());
    }

}

