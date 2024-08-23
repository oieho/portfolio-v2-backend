package com.oieho.jwt.filter;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.oieho.entity.CustomUser;
import com.oieho.entity.ExcessiveLoginAttemptsLock;
import com.oieho.entity.RefreshToken;
import com.oieho.jwt.JwtTokenProvider;
import com.oieho.jwt.SecurityConstants;
import com.oieho.repository.RefreshTokenRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	
	
	long userNo;
	String userId;
	List<String> roles;
	
	@Value("${jwt.refreshExpire}")
	private long refreshExpire; // 864000000 == 10days
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository, RedisTemplate<String, Object> redisTemplate) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.refreshTokenRepository = refreshTokenRepository;
		this.redisTemplate = redisTemplate;
		
		setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);

	}

	@Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        onAuthenticationFailure(request, response, failed);
    }

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authentication) {
		CustomUser user = ((CustomUser) authentication.getPrincipal());	
		
		long userNo = user.getUserNo();
		String userId = user.getUserId();
		String userName = user.getUsername();
		List<String> roles = user.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.toList());
		

		// 성공적으로 인증된 경우 로그인 잠금 실패 횟수 초기화
	    Integer attempts = 0;
	    String key = "login_attempt:" + userName; // 예시: 키를 username에 기반하여 생성
	    Boolean lockLogin = false;
	    ExcessiveLoginAttemptsLock loginUser = new ExcessiveLoginAttemptsLock(userName, attempts, lockLogin);
        redisTemplate.opsForValue().set(key, loginUser);
		
		String accesstoken = jwtTokenProvider.createAccessToken(userNo, userId, userName, roles);
		System.out.println("successfulAuthentication ACCESS::"+accesstoken);
		String refreshtoken = jwtTokenProvider.createRefreshToken(userNo, userId, roles);
		Date expirationTime = new Date(System.currentTimeMillis() + refreshExpire);
		System.out.println("successfulAuthentication REFRESH::"+refreshtoken);
		
		RefreshToken refreshTokenVar = refreshTokenRepository.findByUserId(userId);
		 if (refreshTokenVar == null) {
			 refreshTokenVar = new RefreshToken(userId, refreshtoken);
			 refreshTokenVar.setExpirationTime(expirationTime);
	         refreshTokenRepository.saveAndFlush(refreshTokenVar);
	        } else {
	        	System.out.println("재갱신 리프레시 토큰");
	        	refreshTokenRepository.updateRefreshToken(userId, refreshtoken, expirationTime);
	        }
		 
		response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + accesstoken);
		response.addHeader(SecurityConstants.REFRESH_HEADER, SecurityConstants.REFRESH_PREFIX + refreshtoken);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
			String username = Optional.ofNullable(request.getParameter("username")).orElse("");
	        String password = Optional.ofNullable(request.getParameter("password")).orElse("");
	        ExcessiveLoginAttemptsLock userAttemptsInfo = (ExcessiveLoginAttemptsLock) redisTemplate.opsForValue().get("login_attempt:"+username);
	        Boolean lockLogin;
	    	String key = "login_attempt:" + username;
	    	Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
	    	
	        if (ttl < 0) {
	        	lockLogin = false;
	    	} else {
	    		lockLogin = true;
	    	}
	        
	        System.out.println("lockLogin::"+lockLogin);
	    	if(userAttemptsInfo == null) { // 최초 로그인시 정보가 없으면 정보 저장
	    		Integer attempts = 0;
		        lockLogin = false;
			    ExcessiveLoginAttemptsLock loginUser = new ExcessiveLoginAttemptsLock(username, attempts, lockLogin);
		        redisTemplate.opsForValue().set(key, loginUser);
	    	} else if (lockLogin == true && userAttemptsInfo.getLoginAttempts() == 3) { // 시간이 경과되지 않았으면 위에서 받은 true로 유효성 검사
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setHeader("ttl", String.valueOf(ttl));
				return null;
	    	} else if (lockLogin == false && userAttemptsInfo.getLoginAttempts() == 3) { // 시간이 경과되었으면 위에서 받은 false로 검증하여 로그인 잠금 해제
	        	unlockLogin(request, response, username);
	        }
	    	
	        log.info("username = " + username + " password = " + password);
	        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
	        return authenticationManager.authenticate(authenticationToken);
	}
		
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		String username = Optional.ofNullable(request.getParameter("username")).orElse("");
		ExcessiveLoginAttemptsLock userAttemptsInfo = (ExcessiveLoginAttemptsLock) redisTemplate.opsForValue().get("login_attempt:"+username);
		int maxAttempts = 3;
		String key = "login_attempt:" + username;
		ExcessiveLoginAttemptsLock loginUser;
    	Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
		
		Integer attempts = userAttemptsInfo.getLoginAttempts();
		Boolean lockLogin = userAttemptsInfo.getLockLogin();
		System.out.println("TRUEFALSE::"+userAttemptsInfo.getLockLogin());
		
		if(attempts < 3 && lockLogin == false) {
			attempts++;
			userAttemptsInfo.setLoginAttempts(attempts);
	    	redisTemplate.opsForValue().set(key, userAttemptsInfo);
	    	response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} 
	    if (attempts == maxAttempts && userAttemptsInfo.getLockLogin() == false) {
	    	loginUser = new ExcessiveLoginAttemptsLock(username, attempts, lockLogin);
	    	redisTemplate.opsForValue().set(key, loginUser, 300, TimeUnit.SECONDS); // set은 최초, maxAttempts가 3일 때만 해야 함. 안그러면 ttl(Time to live)이 -1로 초기화 됌.
	    	ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
	    	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    	response.setHeader("ttl", String.valueOf(ttl));
	    }
	}
	
	private void unlockLogin(HttpServletRequest request, HttpServletResponse response, String username) {
		ExcessiveLoginAttemptsLock userAttemptsInfo = (ExcessiveLoginAttemptsLock) redisTemplate.opsForValue().get("login_attempt:"+username);
		
	    if (userAttemptsInfo != null && userAttemptsInfo.getLockLogin() != null) {
	    	String key = "login_attempt:" + username;
	    	long currentTimeMinutes = System.currentTimeMillis() / 1000;
	    	Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS) + currentTimeMinutes;
	        if (currentTimeMinutes >= ttl) {
	        	userAttemptsInfo.setLoginAttempts(0);
	        	userAttemptsInfo.setLockLogin(true);
		        redisTemplate.opsForValue().set(key, userAttemptsInfo);
		        response.setStatus(HttpServletResponse.SC_OK);
	        }
	    } else if (userAttemptsInfo == null || userAttemptsInfo.getLockLogin() == null) {
	        return;
	    }
	}

}