package com.oieho.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.oieho.entity.RoleType;
import com.oieho.jwt.JwtTokenProvider;
import com.oieho.jwt.filter.JwtAuthenticationFilter;
import com.oieho.jwt.filter.JwtRequestFilter;
import com.oieho.oauth.exception.RestAuthenticationEntryPoint;
import com.oieho.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.oieho.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.oieho.oauth.handler.TokenAccessDeniedHandler;
import com.oieho.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.oieho.oauth.service.CustomOAuth2UserService;
import com.oieho.repository.MemberRepository;
import com.oieho.repository.RefreshTokenRepository;
import com.oieho.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final MemberRepository memberRepository;
	private final AppProperties appProperties;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final CustomUserDetailsService cusUserDetailsService;
	private final CustomOAuth2UserService oAuth2UserService;
	private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
	private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;

	@Bean
	static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
		return new OAuth2AuthenticationSuccessHandler(memberRepository, jwtTokenProvider, appProperties, refreshTokenRepository,
				oAuth2AuthorizationRequestBasedOnCookieRepository);
	}

	@Bean
	public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
		return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository);
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://52.78.70.226:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		.csrf(AbstractHttpConfigurer::disable)
		.cors(cors -> cors.configurationSource(corsConfigurationSource()))
		.formLogin(AbstractHttpConfigurer::disable)
		.httpBasic(AbstractHttpConfigurer::disable)
		.authorizeHttpRequests(auth -> auth
		.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
		.requestMatchers("**").permitAll())
		.exceptionHandling(c -> c.authenticationEntryPoint(new RestAuthenticationEntryPoint()).accessDeniedHandler(tokenAccessDeniedHandler))
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.oauth2Login(oauth2 -> oauth2
		.authorizationEndpoint(authorizationEndpointConfigurer -> authorizationEndpointConfigurer.baseUri("/oauth2/authorization").authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository))
		.redirectionEndpoint(redirectionEndpointConfigurer -> redirectionEndpointConfigurer.baseUri("/*/oauth2/code/*"))
		.userInfoEndpoint(userInfoEndpointConfigurer -> userInfoEndpointConfigurer.userService(oAuth2UserService))
		.successHandler(oAuth2AuthenticationSuccessHandler())
		.failureHandler(oAuth2AuthenticationFailureHandler()));

		// AuthenticationManager설정
		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(cusUserDetailsService).passwordEncoder(passwordEncoder());
		// Get AuthenticationManager
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
		http.authenticationManager(authenticationManager)
				.addFilterAt(
						new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider, refreshTokenRepository, redisTemplate),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JwtRequestFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

}
