package com.oieho.jwt;


import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.oieho.entity.AuthToken;
import com.oieho.entity.CustomUser;
import com.oieho.entity.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class JwtTokenProvider {
	
	@Value("${jwt.accessExpire}")
	private long accessExpire; // 3600000 == 1hour
	
	@Value("${jwt.refreshExpire}")
	private long refreshExpire; // 864000000 == 10days
	

	private String secret="236979CB6F1AD6B6A6184A31E6BE37DB3818CC36871E26235DD67DCFE4041492"; //해결과제 : @Value로 불러올 경우 값을 못 불러오는 문제 
    private Key key = Keys.hmacShaKeyFor(secret.getBytes());
	
	@Value("${jwt.authToken}")
    private String authToken;
	
	JwtTokenProvider(String secret) {
		key = Keys.hmacShaKeyFor(secret.getBytes());
    }
	
	public AuthToken validateToken(String token) {
		 return new AuthToken(token, key);
	   }

	
	public AuthToken createAuthToken(long userNo, String userId, String refreshToken, List<String> roles) {
        return new AuthToken(userNo, userId, refreshToken, roles, refreshExpire, key);
    }
	public String createRefreshToken(long userNo, String userId,List<String> roles) {
        return Jwts.builder()
                .setSubject(authToken)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpire))
                .claim("uno", userNo)
                .claim("uid", userId)
    			.claim("rol", roles)
                .compact();
    }
	public long getUserNo(String header) throws Exception {
		String token = header.substring(7);
		byte[] signingKey = getSigningKey();
		Jws<Claims> parsedToken = Jwts.parserBuilder()
			.setSigningKey(signingKey)
			.build()
			.parseClaimsJws(token);
		String subject = parsedToken.getBody().getSubject();
		long userNo = Long.parseLong(subject);
		
		return userNo;
	}
	
	public String createAccessToken(long userNo, String userId, String userName, List<String> roles) {
		byte[] signingKey = getSigningKey();
		
		String token = Jwts.builder()
			.signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
			.setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
			.setExpiration(new Date(System.currentTimeMillis() + accessExpire))
			.claim("uno", "" + userNo)
			.claim("uid", userId)
			.claim("userName", userName)
			.claim("rol", roles)
			.compact();
			
		return token;
	}
	
	public String createNewAccessToken(long userNo, String id, List<String> role) {
		Key signingSecretKey = getSigningSecretKey();
        return Jwts.builder()
                .setSubject(authToken)
                .claim("uno", userNo)
                .claim("uid", id)
                .claim("rol", role)
                .signWith(signingSecretKey, SignatureAlgorithm.HS256)
    			.setExpiration(new Date(System.currentTimeMillis() + accessExpire))
                .compact();
    }
	
//	public String createRefreshToken(String userId) {
//		byte[] signingKey = getRefreshKey();
//        String token = Jwts.builder()
//                .setSubject(userId)
//                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
//                .setExpiration(new Date(System.currentTimeMillis() + refreshExpire))
//                .compact();
//        return token;
//			
//	}
	public UsernamePasswordAuthenticationToken getAuthentication(AuthToken tokenHeader) {
		if (isNotEmpty(tokenHeader.getToken())) {
			try {
				byte[] signingKey = getSigningKey();
				
				Jws<Claims> parsedToken = Jwts.parserBuilder()
					.setSigningKey(signingKey)
					.build()
					.parseClaimsJws(tokenHeader.getToken().replace("Bearer ", ""));
					System.out.println("parsedToken:::"+parsedToken);
					Claims claims = parsedToken.getBody();
					
					String userNo = (String)claims.get("uno");
					String userId = (String)claims.get("uid");
					List<SimpleGrantedAuthority> authorities = ((List<?>) claims.get("rol"))
						.stream()
						.map(authority -> new SimpleGrantedAuthority((String) authority))
						.collect(Collectors.toList());
						System.out.println("userId"+"authorities");
					if (isNotEmpty(userId)) {
						Member member = new Member();
						member.setUserNo(Long.parseLong(userNo));
						member.setUserId(userId);
						member.setUserPw("");
						
						UserDetails userDetails = new CustomUser(member, authorities);
						
						return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
					}
				} catch (ExpiredJwtException exception) {
					log.warn("Request to parse expired JWT : {} failed : {}", tokenHeader, exception.getMessage());
				} catch (UnsupportedJwtException exception) {
					log.warn("Request to parse unsupported JWT : {} failed : {}", tokenHeader, exception.getMessage());
				} catch (MalformedJwtException exception) {
					log.warn("Request to parse invalid JWT : {} failed : {}", tokenHeader, exception.getMessage());
				} catch (IllegalArgumentException exception) {
					log.warn("Request to parse empty or null JWT : {} failed : {}", tokenHeader, exception.getMessage());
				}
			} 
			
			return null;
		}
		
		private byte[] getSigningKey() {
			return authToken.getBytes();
		}
		private Key getSigningSecretKey() {
			return key;
		}
		private boolean isNotEmpty(final CharSequence cs) {
			return !isEmpty(cs);
		}
		
		private boolean isEmpty(final CharSequence cs) {
			return cs == null || cs.length() == 0;
		}
		
	}