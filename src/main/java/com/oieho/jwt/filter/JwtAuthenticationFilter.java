package com.oieho.jwt.filter;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.oieho.entity.CustomUser;
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
	
	long userNo;
	String userId;
	List<String> roles;
	
	@Value("${jwt.refreshExpire}")
	private long refreshExpire; // 864000000 == 10days
	
    private boolean lockTrue = true;
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.refreshTokenRepository = refreshTokenRepository;
		
		setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);

	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		unlockLogin(request); // 로그인 잠금 해제 시도
	    HttpSession session = request.getSession(true);
	    int attempts = Optional.ofNullable((Integer) session.getAttribute("loginAttempts")).orElse(0);
	    int maxAttempts = 3;
	    int lockTime = 5;
	    
	    System.out.println("attempts::"+attempts);
	    if (attempts >= maxAttempts) {
	        try {
				lockLogin(request, response, lockTime);
			} catch (LockedException | IOException e) {
				e.printStackTrace();
			}
	        return null;
	    } else {
	        String username = Optional.ofNullable(request.getParameter("username")).orElse("");
	        String password = Optional.ofNullable(request.getParameter("password")).orElse("");
	        log.info("username = " + username + " password = " + password);
	        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
	        return authenticationManager.authenticate(authenticationToken);
	    }
	}

	private void lockLogin(HttpServletRequest request, HttpServletResponse response, int lockTime) throws LockedException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("loginLocked", true);
        long lockedUntil = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(lockTime);
        if(lockTrue==true) {
        session.setAttribute("lockedUntil", lockedUntil);
        lockTrue = false;
        }
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "로그인이 잠겼습니다. " + lockTime + "분 후에 다시 시도해주세요.");
        throw new LockedException("Login is locked");
    }
	
	private void unlockLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginLocked") != null && (boolean) session.getAttribute("loginLocked")) {
            long lockedUntil = (long) session.getAttribute("lockedUntil");
            if (System.currentTimeMillis() >= lockedUntil) {
                session.removeAttribute("loginLocked");
                session.removeAttribute("lockedUntil");
                session.setAttribute("loginAttempts", 0);
                lockTrue = true;
            }
        }
    }
	
	@Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        onAuthenticationFailure(request, response, failed);
    }

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authentication) {
		// 성공적으로 인증된 경우 실패 횟수 초기화
		HttpSession session = request.getSession(false);
	    if (session != null) {
	        session.setAttribute("loginAttempts", 0);
	        session.setAttribute("loginLocked", false);
	        session.setAttribute("loginLockedTime", 0L);
	    }
	    
		CustomUser user = ((CustomUser) authentication.getPrincipal());	
		
		long userNo = user.getUserNo();
		String userId = user.getUserId();
		String userName = user.getUsername();
		List<String> roles = user.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.toList());
		
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
	
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
	    HttpSession session = request.getSession(false);
	    if (session == null) {
	        session = request.getSession(true);
	    }
	    Integer attempts = (Integer) session.getAttribute("loginAttempts");
	    if (attempts == null) {
	        attempts = 0;
	    }
	    attempts++;
	    session.setAttribute("loginAttempts", attempts);
	}
	
}