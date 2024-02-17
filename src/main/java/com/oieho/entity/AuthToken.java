package com.oieho.entity;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
	private final String token;
	private final Key key;

	@Override
	public String toString() {
		return token;
	}

	public AuthToken(String id, Date expiry, Key key) {
		this.key = key;
		this.token = createAuthToken(id, expiry);
	}

	public AuthToken(long userNo, String userId, List<String> roles, long refreshExpire, Key key) {
		this.key = key;
		this.token = createAuthToken(userNo, userId, roles, refreshExpire);
	}

	private String createAuthToken(String id, Date expiry) {
		return Jwts.builder().setSubject(id).signWith(key, SignatureAlgorithm.HS256)
				.setExpiration(expiry).compact();
	}

	private String createAuthToken(long userNo, String userId, List<String> roles, long refreshExpire) {
		return Jwts.builder().setSubject(userId).signWith(key, SignatureAlgorithm.HS256)
				.setExpiration(new Date(System.currentTimeMillis() + refreshExpire)).claim("uno", userNo)
				.claim("uid", userId).claim("rol", roles).compact();
	}

	public boolean validationCheck() {
		System.out.println("validationCheck:::"+this.getTokenClaims());
		return this.getTokenClaims() == null;
	}

	public Claims getTokenClaims() {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
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
		return null;
	}

	public Claims getExpiredTokenClaims(String oldToken) {
		try {
			return Optional
					.ofNullable(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(oldToken).getBody())
					.orElseThrow(() -> null);
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token.");
			return null;
		}
	}

}
