package kr.co.peopleinsoft.cmmn.security.filter;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.peopleinsoft.cmmn.security.authentication.CmmnUserDetailsService;
import kr.co.peopleinsoft.cmmn.security.token.CmmnRFC6750;
import kr.co.peopleinsoft.cmmn.security.token.CmmnSecureTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * JWT 등의 인증 처리 로직이 필요한 경우 사용 (HttpSecurity 에 필터로 등록하여 사용)
 * ex) http.addFilterBefore(CmmnPostQuantumAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
 */
public class CmmnPostQuantumAuthenticationFilter extends OncePerRequestFilter {

	static final Logger logger = LoggerFactory.getLogger(CmmnPostQuantumAuthenticationFilter.class);

	final CmmnUserDetailsService cmmnUserDetailsService;
	final CmmnSecureTokenProvider cmmnSecureTokenProvider;

	public CmmnPostQuantumAuthenticationFilter(CmmnUserDetailsService cmmnUserDetailsService, CmmnSecureTokenProvider cmmnSecureTokenProvider) {
		this.cmmnUserDetailsService = cmmnUserDetailsService;
		this.cmmnSecureTokenProvider = cmmnSecureTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		String secureToken;

		// Request Header 또는 Session 에 저장된 SecureToken 값 조회
		if (bearerToken != null && bearerToken.startsWith(CmmnRFC6750.BEARER.getValue())) {
			secureToken = bearerToken.substring(7);
		} else {
			secureToken = (String) session.getAttribute("SESSION-X-AUTH-TOKEN");
		}

		// 조회된 SecureToken 이 있으면 해당 토큰으로 승인 처리 진행
		if (StringUtils.isNotBlank(secureToken)) {
			processAuthorization(secureToken, request, response);
		}

		filterChain.doFilter(request, response);
	}

	/*** Token 기반 사용자 정보 조회를 통한 승인 처리 진행 ***/
	void processAuthorization(String secureToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpSessionRequestCache cache = new HttpSessionRequestCache();
			SavedRequest savedRequest = cache.getRequest(request, response);

			if (cmmnSecureTokenProvider.verifySecureToken(secureToken)) {
				// 토큰이 만료되지 않았다면 사용자 정보를 기반으로 인증 처리
				Map<String, Object> map = cmmnSecureTokenProvider.decryptToken(secureToken);
				String userName = String.valueOf(map.get("sub"));

				UserDetails userDetails = cmmnUserDetailsService.loadUserByUsername(userName);
				Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				// 토큰이 만료되었다면 로그아웃 처리
				new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
				SecurityContextHolder.clearContext();
				request.logout();

				if (savedRequest != null) {
					String targetUrl = savedRequest.getRedirectUrl();
					if (org.apache.commons.lang3.StringUtils.isNotBlank(targetUrl) && !targetUrl.contains("/login")) {
						cache.removeRequest(request, response);
						response.sendRedirect(targetUrl);
					} else {
						response.sendRedirect(request.getContextPath() + "/");
					}
				}
			}
		} catch (Exception e) {
			SecurityContextHolder.clearContext();
			logger.error(e.getMessage(), e);
		}
	}
}