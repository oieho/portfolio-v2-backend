package com.oieho.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.oieho.entity.AuthToken;
import com.oieho.jwt.HeaderUtil;
import com.oieho.jwt.JwtTokenProvider;
import com.oieho.jwt.SecurityConstants;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException, java.io.IOException{
		  String tokenStr = HeaderUtil.getAccessToken(request);
	      AuthToken token = jwtTokenProvider.convertToValidatingToken(tokenStr);
		if (isEmpty(token.getToken()) || !token.getToken().startsWith(SecurityConstants.TOKEN_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}
		
			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			System.out.println("authentication:::"+authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);
		return;
		}
	private boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}
	}