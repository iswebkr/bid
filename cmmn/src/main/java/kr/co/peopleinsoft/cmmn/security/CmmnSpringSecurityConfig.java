package kr.co.peopleinsoft.cmmn.security;

import kr.co.peopleinsoft.cmmn.security.authentication.CmmnUserDetailsService;
import kr.co.peopleinsoft.cmmn.security.authorization.CmmnRequestAuthorizationManager;
import kr.co.peopleinsoft.cmmn.security.config.CmmnSecurityConfig;
import kr.co.peopleinsoft.cmmn.security.filter.CmmnPostQuantumAuthenticationFilter;
import kr.co.peopleinsoft.cmmn.security.handler.CmmnAuthenticationFailureHandler;
import kr.co.peopleinsoft.cmmn.security.handler.CmmnAuthenticationSuccessHandler;
import kr.co.peopleinsoft.cmmn.security.hierarchy.CmmnRoleHierarchy;
import kr.co.peopleinsoft.cmmn.security.token.CmmnSecureTokenProvider;
import kr.co.peopleinsoft.cmmn.security.token.provider.CmmnDigitalSignProvider;
import kr.co.peopleinsoft.cmmn.security.token.provider.CmmnJweProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class CmmnSpringSecurityConfig {

	final ConfigurableEnvironment environment;
	final NamedParameterJdbcTemplate jdbcTemplate;

	public CmmnSpringSecurityConfig(ConfigurableEnvironment environment, NamedParameterJdbcTemplate jdbcTemplate) {
		this.environment = environment;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Bean
	@Order(-999)
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) {
		return http.securityMatcher("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
			.csrf(CsrfConfigurer::disable)
			.headers(cmmnSecurityConfig().headersConfigurerCustomizer())
			.sessionManagement(cmmnSecurityConfig().sessionConfigurerCustomizer())
			.authorizeHttpRequests(request -> {
				request.anyRequest().permitAll();
			}).build();
	}

	@Bean
	@Order(-998)
	SecurityFilterChain webSocketSecurityFilterChain(HttpSecurity http) {
		return http.securityMatcher("/ws/**")
			.csrf(CsrfConfigurer::disable)
			.authorizeHttpRequests(request -> {
				request.anyRequest().authenticated();
			}).build();
	}

	/*** JWE(JSON Web Encryption) provider ***/
	@Bean
	CmmnJweProvider cmmnJweProvider() {
		return new CmmnJweProvider();
	}

	/*** Dilithium 전자서명 Provider ***/
	@Bean
	CmmnDigitalSignProvider cmmnDigitalSignProvider() {
		return new CmmnDigitalSignProvider();
	}

	@Bean
	CmmnSecureTokenProvider cmmnSecureTokenProvider(CmmnJweProvider cmmnJweProvider, CmmnDigitalSignProvider cmmnDigitalSignProvider) {
		return new CmmnSecureTokenProvider(cmmnJweProvider, cmmnDigitalSignProvider);
	}

	/*** 계층별 권한에 따른 접근 처리를 위한 bean ***/
	@Bean
	CmmnRoleHierarchy roleHierarchy() {
		return new CmmnRoleHierarchy(environment, jdbcTemplate);
	}

	/*** HttpFirewall 설정 ***/
	@Bean
	HttpFirewall httpFirewall() {
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowSemicolon(true); // 세미콜론 차단해제
		firewall.setAllowUrlEncodedSlash(false); // 인코딩된 슬래시 차단
		firewall.setAllowBackSlash(false); // 백슬래시 차단
		firewall.setAllowUrlEncodedPercent(false); // 인코딩된 퍼센트 차단
		firewall.setAllowUrlEncodedPeriod(false); // 인코딩된 마침표 차단
		firewall.setAllowNull(false); // null 차단

		// 특정 HTTP 메서드만 허용
		firewall.setAllowedHttpMethods(Arrays.asList(
			"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
		));

		return firewall;
	}

	/*** WebSecurity Customizer ***/
	@Bean
	WebSecurityCustomizer webSecurityCustomizer(HttpFirewall httpFirewall) {
		return (web) -> web.httpFirewall(httpFirewall);
	}

	/*** Password Encoder ***/
	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	/*** UserDetailService ***/
	@Bean
	CmmnUserDetailsService cmmnUserDetailsService(PasswordEncoder passwordEncoder) {
		return new CmmnUserDetailsService(environment, jdbcTemplate, passwordEncoder);
	}

	/**
	 * JWE + Dilithium 전자서명 처리
	 * SecurityFilterChain 에 아래와 같은 형태로 해당 필터가 등록되어야 함
	 * http.addFilterBefore(cmmnPostQuantumAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	 */
	@Bean
	CmmnPostQuantumAuthenticationFilter cmmnPostQuantumAuthenticationFilter(CmmnUserDetailsService cmmnUserDetailsService, CmmnSecureTokenProvider cmmnSecureTokenProvider) {
		return new CmmnPostQuantumAuthenticationFilter(cmmnUserDetailsService, cmmnSecureTokenProvider);
	}

	/*** 인가관리 ***/
	@Bean
	AuthorizationManager<RequestAuthorizationContext> cmmnRequestAuthorizationManager(CmmnRoleHierarchy roleHierarchy) {
		return new CmmnRequestAuthorizationManager(roleHierarchy, jdbcTemplate);
	}

	/*** 인증 성공시 Handler ***/
	@Bean
	CmmnAuthenticationSuccessHandler cmmnAuthenticationSuccessHandler(CmmnUserDetailsService cmmnUserDetailsService, CmmnSecureTokenProvider cmmnSecureTokenProvider) {
		return new CmmnAuthenticationSuccessHandler(cmmnUserDetailsService, cmmnSecureTokenProvider);
	}

	/*** 인증 실패시 Handler ***/
	@Bean
	CmmnAuthenticationFailureHandler cmmnAuthenticationFailureHandler() {
		return new CmmnAuthenticationFailureHandler();
	}

	/*** SecurityFilterChain 에서 사용되는 설정 정보 모음 ***/
	@Bean
	CmmnSecurityConfig cmmnSecurityConfig() {
		return new CmmnSecurityConfig();
	}
}