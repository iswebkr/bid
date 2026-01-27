package kr.co.peopleinsoft.init;

import kr.co.peopleinsoft.cmmn.security.config.CmmnSecurityConfig;
import kr.co.peopleinsoft.cmmn.security.filter.CmmnPostQuantumAuthenticationFilter;
import kr.co.peopleinsoft.cmmn.security.handler.CmmnAuthenticationFailureHandler;
import kr.co.peopleinsoft.cmmn.security.handler.CmmnAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class BidSecurityConfigurer {
	final CmmnPostQuantumAuthenticationFilter cmmnPostQuantumAuthenticationFilter;
	final CmmnAuthenticationSuccessHandler cmmnAuthenticationSuccessHandler;
	final CmmnAuthenticationFailureHandler cmmnAuthenticationFailureHandler;
	final CmmnSecurityConfig cmmnSecurityConfig;

	public BidSecurityConfigurer(CmmnPostQuantumAuthenticationFilter cmmnPostQuantumAuthenticationFilter, CmmnAuthenticationSuccessHandler cmmnAuthenticationSuccessHandler, CmmnAuthenticationFailureHandler cmmnAuthenticationFailureHandler, CmmnSecurityConfig cmmnSecurityConfig) {
		this.cmmnPostQuantumAuthenticationFilter = cmmnPostQuantumAuthenticationFilter;
		this.cmmnAuthenticationSuccessHandler = cmmnAuthenticationSuccessHandler;
		this.cmmnAuthenticationFailureHandler = cmmnAuthenticationFailureHandler;
		this.cmmnSecurityConfig = cmmnSecurityConfig;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizationManager<RequestAuthorizationContext> requestAuthorizationContext) {
		http.csrf(CsrfConfigurer::disable);
		http.addFilterBefore(cmmnPostQuantumAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		http.formLogin(form -> form
			// .loginPage("/login")
			.successHandler(cmmnAuthenticationSuccessHandler)
			.failureHandler(cmmnAuthenticationFailureHandler));
		http.authorizeHttpRequests(authorize -> authorize
			.requestMatchers("/login", "/bid").permitAll()
			.anyRequest()
			.access(requestAuthorizationContext)
		);
		http.headers(cmmnSecurityConfig.headersConfigurerCustomizer());
		http.sessionManagement(cmmnSecurityConfig.sessionConfigurerCustomizer());
		return http.build();
	}
}