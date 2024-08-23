package com.oieho.jwt;

// 토큰 명은 특수문자, 공백, CamelCase를 브라우저에서 인식하지 못함, 따라서 PascalCase로 작성
public final class SecurityConstants {
	public static final String TOKEN_HEADER = "authorization";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String REFRESH_HEADER = "refreshauth";
	public static final String REFRESH_PREFIX = "Bearer ";
	
	public static final String TOKEN_TYPE = "JWT";
	public static final String AUTH_LOGIN_URL = "/auth/authenticate";
	
	public static final String invalidAllTokens  = "invalidalltokens";
	} 