package com.oieho.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

        configuration.addAllowedOrigin("https://web-portfolio-v2-frontend-3prof2lll3bfr1i.sel3.cloudtype.app");
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
        .cors().and().csrf().disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    .and()
        .formLogin().disable()
        .httpBasic().disable()
        .exceptionHandling()
        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
        .accessDeniedHandler(tokenAccessDeniedHandler)
    .and()
        .authorizeRequests()
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
        .antMatchers("*").hasAnyAuthority(RoleType.USER.getCode())
        .antMatchers("/api/**/admin/**").hasAnyAuthority(RoleType.ADMIN.getCode())

    .and()
        .oauth2Login()
        .authorizationEndpoint()
        .baseUri("/oauth2/authorization/*")
        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository)
    .and()
        .redirectionEndpoint()
        .baseUri("/*/oauth2/code/*")
    .and()
        .userInfoEndpoint()
        .userService(oAuth2UserService)
    .and()
        .successHandler(oAuth2AuthenticationSuccessHandler())
        .failureHandler(oAuth2AuthenticationFailureHandler());

		// AuthenticationManager설정
		AuthenticationManagerBuilder authenticationManagerBuilder = http
				.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.userDetailsService(cusUserDetailsService).passwordEncoder(passwordEncoder());
		// Get AuthenticationManager
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
		http.authenticationManager(authenticationManager)
				.addFilterAt(
						new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider, refreshTokenRepository),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JwtRequestFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

}
