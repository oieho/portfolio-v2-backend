package com.oieho.jwt;

public final class SecurityConstants {
	public static final String TOKEN_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String REFRESH_HEADER = "RefreshAuthorization";
	public static final String REFRESH_PREFIX = "Bearer ";
	
	public static final String TOKEN_TYPE = "JWT";
	public static final String AUTH_LOGIN_URL = "/auth/authenticate";
	
	public static final String invalidAccessToken = "Invalid access token.";
	public static final String invalidRefreshToken = "Invalid refresh token.";
	public static final String invalidAllTokens  = "Invalid access and refresh tokens.";
	public static final String notExpiredTokenYet = "Not expired token yet.";
	} 