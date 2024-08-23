package com.oieho.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public final class JwtTokenProvider {

	private final JwtConfig jwtConfig;

	String payload = null;

	private String getBase64Payload(String[] extractedBase64Payload) {
		Base64.Decoder decoder = Base64.getUrlDecoder();
		payload = new String(decoder.decode(extractedBase64Payload[1]));

		return payload;
	}

	public Map<String, Object> extractJWTClaims(String token) {
		Map<String, Object> extractedJwtClaims = new HashMap<>();
		String[] base64Payload = token.split("\\.");
		payload = getBase64Payload(base64Payload);
		ObjectMapper mapper = new ObjectMapper();
		try {
			extractedJwtClaims = mapper.readValue(payload, Map.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return extractedJwtClaims;
	}

	public AuthToken convertToValidatingToken(String token) {
		Key signingSecretKey = getSigningSecretKey();
		return new AuthToken(token, signingSecretKey);
	}

	public AuthToken createAuthToken(long userNo, String userId, List<String> roles) {
		return new AuthToken(userNo, userId, roles, jwtConfig.getRefreshExpire(), jwtConfig.getHmacShaKey());
	}

	public String createRefreshToken(long userNo, String userId, List<String> roles) {
		return Jwts.builder().setSubject(userId).signWith(jwtConfig.getHmacShaKey(), SignatureAlgorithm.HS256)
				.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshExpire())).claim("uno", userNo)
				.claim("uid", userId).claim("rol", roles).compact();
	}

	public long getUserNo(String header) throws Exception {
		String token = header.substring(7);
		byte[] signingKey = getSigningKey();
		Jws<Claims> parsedToken = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
		String subject = parsedToken.getBody().getSubject();
		long userNo = Long.parseLong(subject);

		return userNo;
	}

	public String createAccessToken(long userNo, String userId, String userName, List<String> roles) {
		byte[] signingKey = getSigningKey();
		return Jwts.builder().signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
				.setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
				.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessExpire()))
				.claim("uno", "" + userNo).claim("uid", userId).claim("userName", userName).claim("rol", roles)
				.compact();
	}

	public String createNewAccessToken(long userNo, String id, List<String> role) {
		Key signingSecretKey = getSigningSecretKey();
		return Jwts.builder().claim("uno", userNo).claim("uid", id)
				.claim("rol", role).signWith(signingSecretKey, SignatureAlgorithm.HS256)
				.setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessExpire())).compact();
	}

	public UsernamePasswordAuthenticationToken getAuthentication(AuthToken tokenHeader) {
		if (isNotEmpty(tokenHeader.getToken())) {
			try {
				byte[] signingKey = getSigningKey();

				Jws<Claims> parsedToken = Jwts.parserBuilder().setSigningKey(signingKey).build()
						.parseClaimsJws(tokenHeader.getToken().replace("Bearer ", ""));
				System.out.println("parsedToken:::" + parsedToken);
				Claims claims = parsedToken.getBody();

				String userNo = (String) claims.get("uno");
				String userId = (String) claims.get("uid");
				List<SimpleGrantedAuthority> authorities = ((List<?>) claims.get("rol")).stream()
						.map(authority -> new SimpleGrantedAuthority((String) authority)).collect(Collectors.toList());
				System.out.println("userId" + "authorities");
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
		return jwtConfig.getAuthToken().getBytes();
	}

	private Key getSigningSecretKey() {
		return jwtConfig.getHmacShaKey();
	}

	private boolean isNotEmpty(final CharSequence cs) {
		return !isEmpty(cs);
	}

	private boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

}