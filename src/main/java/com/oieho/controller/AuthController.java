package com.oieho.controller;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oieho.entity.AuthToken;
import com.oieho.entity.RefreshToken;
import com.oieho.jwt.AuthenticationRequest;
import com.oieho.jwt.CookieUtil;
import com.oieho.jwt.JwtTokenProvider;
import com.oieho.jwt.SecurityConstants;
import com.oieho.repository.RefreshTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	
	@Value("${jwt.authToken}")
    private String authToken;
	
	@Value("${jwt.authToken}")
    private String refreshToken;
	
	private String secret="236979CB6F1AD6B6A6184A31E6BE37DB3818CC36871E26235DD67DCFE4041492"; //해결과제 : @Value로 불러올 경우 값을 못 불러오는 문제 
	private Key key = Keys.hmacShaKeyFor(secret.getBytes());
	
	@PostMapping("/authenticate")//메서드 내부에 아무 것도 없어도 ID/PW인증 가능
	public ResponseEntity<String> login(@RequestBody AuthenticationRequest authenticationRequest,HttpServletRequest request,HttpServletResponse response) throws InterruptedException {
		String username = authenticationRequest.getUsername();
		String password = authenticationRequest.getPassword();
		log.info("username = " + username + " password = " + password);

		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@GetMapping(value="/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request,HttpServletResponse response)  throws Exception{
		final long THREE_DAYS_MSEC = 259200000;
		final String ACCESS_TOKEN = "accessToken";
		final String REFRESH_TOKEN = "refreshToken";

		String oldAccessToken = CookieUtil.getCookie(request, ACCESS_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
		String oldRefreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
	                .map(Cookie::getValue)
	                .orElse((null));
		System.out.println("oldAccessToken:::"+oldAccessToken);
		System.out.println("oldRefreshToken:::"+oldRefreshToken);
		//기존 리프레시 토큰에서 claims 추출하기 위한 코드
		String[] base64Payload = null;
		if((oldAccessToken == null & oldRefreshToken == null) || (oldAccessToken.equals("undefined") | oldRefreshToken.equals("undefined"))) { // 로그인 안한 상태에서 새로고침시 null 값 처리하여 exception 예방
			System.out.println("All tokens are expired.");
			return new ResponseEntity<String>(SecurityConstants.invalidAllTokens,HttpStatus.OK);
		}
		if(oldAccessToken.equals("null")) {
			base64Payload = oldRefreshToken.split("\\.");
		} else {
			base64Payload = oldAccessToken.split("\\.");
		}
		
		if (oldRefreshToken.equals("null")) {
			base64Payload = oldAccessToken.split("\\.");
			System.out.println("oldrefresh");
		} else {
			base64Payload = oldRefreshToken.split("\\.");
		}
		
		Base64.Decoder decoder = Base64.getUrlDecoder();
		System.out.println("base64payload[1]:::"+base64Payload[1]);
		String payload = new String(decoder.decode(base64Payload[1]));
		System.out.println("payload"+payload);
		Map<String, Object> returnMap = null;
		ObjectMapper mapper = new ObjectMapper();
	    try {
			returnMap = mapper.readValue(payload, Map.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	    List<String> roles = (List<String>) returnMap.get("rol");
		
		AuthToken accessToken = jwtTokenProvider.validateToken(oldAccessToken);
		AuthToken RefreshToken = jwtTokenProvider.validateToken(oldRefreshToken);
		Claims accessClaims = accessToken.getExpiredTokenClaims(oldAccessToken); // accessToken payload는 유효성 검증에 통과되지 않아 null 리턴;
		Claims refreshClaims = RefreshToken.getExpiredTokenClaims(oldRefreshToken);;
		System.out.println("accessClaims:::"+accessClaims);
		System.out.println("refreshClaims:::"+refreshClaims);
		Date expirationTime = null;
		String userId = null;
		if(refreshClaims != null) {
			userId = (String) refreshClaims.get("uid");
		}
		
		if (accessClaims == null & refreshClaims == null) { //두 토큰이 만료된 후 새로고침되면 토큰 재발급을 못하게 설정
			
			System.out.println("All tokens are expired.");
			return new ResponseEntity<String>(SecurityConstants.invalidAllTokens,HttpStatus.OK);
		}
		if (accessClaims == null) { // accessToken 이 만료될 경우
			if (!RefreshToken.validate()) {
				System.out.println("All tokens Are invalid");
				return new ResponseEntity<String>(SecurityConstants.invalidAllTokens,HttpStatus.OK); // access, refresh tokens are invalid.
			} else if(refreshClaims != null){
				System.out.println("returnMap::: "+returnMap);
				long userNo = Long.parseLong(String.valueOf(returnMap.get("uno")));
				userId = (String) returnMap.get("uid");
				RefreshToken newUserRefreshToken = refreshTokenRepository.findByUserIdAndRefreshToken(userId, oldRefreshToken);
				System.out.println("userId   "+userId+"   newUserRefreshToken::"+newUserRefreshToken+"           :::oldRefresh:::"+oldRefreshToken);
				if (newUserRefreshToken == null) {
					System.out.println("리프레시 토큰이 DB에 존재하지 않아 액세스 토큰을 발행하지 않습니다.");
					return new ResponseEntity<String>(SecurityConstants.invalidRefreshToken,HttpStatus.OK);
				} else {
					String newAccessToken = jwtTokenProvider.createNewAccessToken(userNo, userId, roles);
					System.out.println("Access Token 이 만료되고 Refresh Token이 유효하면 Access Token 재발행");
					System.out.println(newAccessToken);
					response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + newAccessToken);
					return new ResponseEntity<String>(HttpStatus.OK);
				}
			} 
		}
		if (refreshClaims == null) { // refreshToken 이 만료될 경우
			if (!accessToken.validate()) { //[유효하지 않음 = !accessToken.validate()]
				System.out.println("유효하지 않은 액세스 토큰");
	            return new ResponseEntity<String>(SecurityConstants.invalidAccessToken,HttpStatus.OK);
	        } else {
	        	long userNo = Long.parseLong(String.valueOf(returnMap.get("uno")));
	        	userId = (String) returnMap.get("uid");
	    		
	        	System.out.println("엑세스 토큰이 유효하면 검증하여 리프레시 토큰 재발급");
	    		String newRefreshtoken = jwtTokenProvider.createRefreshToken(userNo, userId, roles);
	    		expirationTime = new Date(Jwts.parserBuilder()
	    				.setSigningKey(key)
	    				.build()
	    				.parseClaimsJws(newRefreshtoken)
	    				.getBody().getExpiration().getTime());
	        	refreshTokenRepository.updateRefreshToken(userId, newRefreshtoken, expirationTime);
	        	System.out.println("newRefreshtoken::"+newRefreshtoken);
		        response.addHeader(SecurityConstants.REFRESH_HEADER, SecurityConstants.REFRESH_PREFIX + newRefreshtoken);
				return new ResponseEntity<String>(HttpStatus.OK);
	        }
		}
		
		// access,refresh가 만료되지 않았으며 리프레시토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
		long validTime = refreshClaims.getExpiration().getTime() - System.currentTimeMillis();
		if (validTime <= THREE_DAYS_MSEC) {
			long userNo = Long.parseLong(String.valueOf(returnMap.get("uno")));
			userId = (String) returnMap.get("uid");
			
			AuthToken newRefreshToken = jwtTokenProvider.createAuthToken(userNo, userId, refreshToken, roles);
			System.out.println("newRefreshToken:::"+newRefreshToken);
        	// DB에 refresh 토큰 업데이트
        	String updateRefreshToStr = newRefreshToken.getToken();
    		expirationTime = new Date(Jwts.parserBuilder()
    				.setSigningKey(key)
    				.build()
    				.parseClaimsJws(updateRefreshToStr)
    				.getBody().getExpiration().getTime());
        	refreshTokenRepository.updateRefreshToken(userId, updateRefreshToStr, expirationTime);
        	//@GetMapping("/myinfo")에서는 이전(처음 로그인할 경우[successfulAuthentication], 세 번째 새로고침일 경우[이전에 메서드 내부에서 얻은 토큰])에 발급 받은 토큰으로 유효성 검증
            System.out.println("3일 이하 유효기간 리프레시 토큰 재발급 : "+newRefreshToken);
            response.addHeader(SecurityConstants.REFRESH_HEADER, SecurityConstants.REFRESH_PREFIX + newRefreshToken);
		}
		System.out.println("access/refresh tokens are valid");
		return new ResponseEntity<String>(HttpStatus.OK);
    }
}
