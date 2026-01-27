package kr.co.peopleinsoft.cmmn.security.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/***
 * SecurityFilterChain 의 Security Matchers 에 공통된 설정을 사용하고자 하는 경우
 * 이 클래스의 메서드를 사용
 * <code>
 * SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, CmmnSecurityConfig cmmnSecurityConfig) throws Exception {
 * 		http.securityMatcher("/sample/**")
 * 			.csrf(cmmnSecurityConfig.csrfConfigurerCustomizer())
 * 		    .header(cmmnSecurityConfig.headersConfigurerCustomizer())
 * 		    .session(cmmnSecurityConfig.sessionConfigurerCustomizer());
 * 		return http.build();
 *  }
 * 	</code>
 */
public class CmmnSecurityConfig {

	static final Logger logger = LoggerFactory.getLogger(CmmnSecurityConfig.class);

	/**
	 * SpringSecurity CORS 기본 설정 (공통으로 사용하는 경우 이 메서드를 사용)
	 */
	public Customizer<CorsConfigurer<HttpSecurity>> corsConfigurerCustomizer() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
		corsConfiguration.setAllowedHeaders(List.of("*"));
		corsConfiguration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
		configurationSource.registerCorsConfiguration("/**", corsConfiguration);

		return cors -> {
			cors.configurationSource(configurationSource);
		};
	}

	/**
	 * Springsecurity CSRF 기본 설정 (공통으로 사용하는 경우 이 메서드를 사용)
	 */
	public Customizer<CsrfConfigurer<HttpSecurity>> csrfConfigurerCustomizer() {
		return csrf -> {
			csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		};
	}

	/**
	 * SpringSecurity Header 기본 설정 (공통으로 사용하는 경우 이 메서드를 사용)
	 */
	public Customizer<HeadersConfigurer<HttpSecurity>> headersConfigurerCustomizer() {
		return header -> {
			header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
		};
	}

	/**
	 * SpringSecurity Session 기본 설정 (공통으로 사용하는 경우 이 메서드를 사용)
	 */
	public Customizer<SessionManagementConfigurer<HttpSecurity>> sessionConfigurerCustomizer() {
		return session -> {
			session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
			session.maximumSessions(1) // 동시 세션 제한
				.maxSessionsPreventsLogin(false) // 중복 로그인 처리
				.expiredUrl("/login?expired")
				.sessionRegistry(new SessionRegistryImpl())
				.expiredSessionStrategy((expiredEvent) -> {
					DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
					redirectStrategy.sendRedirect(expiredEvent.getRequest(), expiredEvent.getResponse(), "/login?expired");
				});
			session.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession); // 세션 고정 공격 방어
		};
	}
}