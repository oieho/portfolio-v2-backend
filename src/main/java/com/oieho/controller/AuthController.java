package com.oieho.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.oieho.entity.AuthToken;
import com.oieho.entity.RefreshToken;
import com.oieho.jwt.AuthenticationRequest;
import com.oieho.jwt.CookieUtil;
import com.oieho.jwt.JwtConfig;
import com.oieho.jwt.JwtTokenProvider;
import com.oieho.jwt.SecurityConstants;
import com.oieho.repository.RefreshTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final JwtConfig jwtConfig;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	@PostMapping("/authenticate") // 메서드 내부에 아무 것도 없어도 ID/PW인증 가능
	public ResponseEntity<String> login(@RequestBody AuthenticationRequest authenticationRequest,
			HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
		String username = authenticationRequest.getUsername();
		String password = authenticationRequest.getPassword();
		log.info("username = " + username + " password = " + password);

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@GetMapping(value = "/refresh")
	public ResponseEntity<String> refresh(HttpServletRequest request, HttpServletResponse response) throws Exception {
		final long THREE_DAYS_MSEC = 259200000;
		Boolean invalidChk = false;

		final String oldAccessToken = CookieUtil.getCookie(request, "accessToken").map(Cookie::getValue).orElse((null));
		final String oldRefreshToken = CookieUtil.getCookie(request, "refreshToken").map(Cookie::getValue).orElse((null));
		System.out.println("oldAccessToken:::" + oldAccessToken);
		System.out.println("oldRefreshToken:::" + oldRefreshToken);

		Date expirationTime = null;
		String userId = null;
		Long userNo = null;
		List<String> roles = null;
		Map<String, Object> extractedJwtClaims = null;

		Claims accessClaims = null;
		Claims refreshClaims = null;
		AuthToken accessToken = null;
		AuthToken refreshToken = null;

		if ((oldAccessToken == null && oldRefreshToken == null)
				|| (oldAccessToken.equals("undefined") && oldRefreshToken.equals("undefined"))) { //undefined 체크를 안하면 초기 페이지 렌더링시에 java.lang.ArrayIndexOutOfBoundsException 발생
			invalidChk = true;
		} else {
			if ((oldAccessToken != null)) {
				accessToken = jwtTokenProvider.convertToValidatingToken(oldAccessToken);
				extractedJwtClaims = jwtTokenProvider.extractJWTClaims(oldAccessToken);
				accessClaims = accessToken.getExpiredTokenClaims(oldAccessToken);
			} 
			if ((oldRefreshToken != null)) {
				refreshToken = jwtTokenProvider.convertToValidatingToken(oldRefreshToken);
				extractedJwtClaims = jwtTokenProvider.extractJWTClaims(oldRefreshToken);
				refreshClaims = refreshToken.getExpiredTokenClaims(oldRefreshToken);
			}
		}
		System.out.println("accessClaims:::" + accessClaims);
		System.out.println("refreshClaims:::" + refreshClaims);

		if (accessClaims == null) { // accessToken 이 만료될 경우
			if (refreshToken != null && refreshToken.validationCheck()) {
				invalidChk = true;
			} else if(extractedJwtClaims != null) {
				userNo = Long.parseLong(String.valueOf(extractedJwtClaims.get("uno")));
				userId = (String) extractedJwtClaims.get("uid");
				roles = (List<String>) extractedJwtClaims.get("rol");
				RefreshToken newUserRefreshToken = refreshTokenRepository.findByUserIdAndRefreshToken(userId,
						oldRefreshToken);
				System.out.println("userId   " + userId + "   newUserRefreshToken::" + newUserRefreshToken
						+ "           :::oldRefresh:::" + oldRefreshToken);
				if (newUserRefreshToken == null) {
					System.out.println("리프레시 토큰이 DB에 존재하지 않아 액세스 토큰을 발급하지 않습니다.");
					invalidChk = true;
				} else {
					String newAccessToken = jwtTokenProvider.createNewAccessToken(userNo, userId, roles);
					System.out.println("Access Token 이 만료되고 Refresh Token이 유효하면 Access Token 재발급");
					System.out.println("NewAccessToken::" + newAccessToken);
					response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + newAccessToken);
					return new ResponseEntity<String>(HttpStatus.OK);
				}
			}
		} else if (refreshClaims == null) { // refreshToken 이 만료될 경우
			if (accessToken != null && accessToken.validationCheck()) { //!accessToken.validate()  false를 반환할 때 실행됩니다.
				invalidChk = true;
			} else if(extractedJwtClaims != null){
				userNo = Long.parseLong(String.valueOf(extractedJwtClaims.get("uno")));
				userId = (String) extractedJwtClaims.get("uid");
				roles = (List<String>) extractedJwtClaims.get("rol");
				System.out.println("Refresh 토큰이 만료되고 AccessToken이 유효하면 검증하여 RefreshToken 재발급");
				String newRefreshtoken = jwtTokenProvider.createRefreshToken(userNo, userId, roles);
				expirationTime = new Date(Jwts.parserBuilder().setSigningKey(jwtConfig.getHmacShaKey()).build()
						.parseClaimsJws(newRefreshtoken).getBody().getExpiration().getTime());
				refreshTokenRepository.updateRefreshToken(userId, newRefreshtoken, expirationTime);
				System.out.println("newRefreshtoken::" + newRefreshtoken);
				response.addHeader(SecurityConstants.REFRESH_HEADER,
						SecurityConstants.REFRESH_PREFIX + newRefreshtoken);
				return new ResponseEntity<String>(HttpStatus.OK);
			}
		}
		
		if(invalidChk == true) {
			response.addHeader(SecurityConstants.invalidAllTokens, "invalid");
			return new ResponseEntity<String>(HttpStatus.OK);
		}
		
		// access,refresh가 만료되지 않았으며 리프레시토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
		long validTime = refreshClaims.getExpiration().getTime() - System.currentTimeMillis();
		if (validTime <= THREE_DAYS_MSEC) {
			userNo = Long.parseLong(String.valueOf(extractedJwtClaims.get("uno")));
			userId = (String) extractedJwtClaims.get("uid");
			roles = (List<String>) extractedJwtClaims.get("rol");

			AuthToken newRefreshToken = jwtTokenProvider.createAuthToken(userNo, userId, roles);
			System.out.println("3일 이하 유효기간 리프레시 토큰 재발급 : " + newRefreshToken);

			// DB에 refresh 토큰 업데이트
			String updateRefreshToStr = newRefreshToken.getToken();
			expirationTime = new Date(Jwts.parserBuilder().setSigningKey(jwtConfig.getHmacShaKey()).build()
					.parseClaimsJws(updateRefreshToStr).getBody().getExpiration().getTime());
			refreshTokenRepository.updateRefreshToken(userId, updateRefreshToStr, expirationTime);

			response.addHeader(SecurityConstants.REFRESH_HEADER, SecurityConstants.REFRESH_PREFIX + newRefreshToken);
		}
		System.out.println("access/refresh Tokens are valid");
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
