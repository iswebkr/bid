package kr.co.peopleinsoft.cmmn.security.handler;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.peopleinsoft.cmmn.security.authentication.CmmnUserDetailsService;
import kr.co.peopleinsoft.cmmn.security.token.CmmnRFC6750;
import kr.co.peopleinsoft.cmmn.security.token.CmmnSecureTokenProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;
import java.text.ParseException;

public class CmmnAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	final CmmnUserDetailsService cmmnUserDetailsService;
	final CmmnSecureTokenProvider cmmnSecureTokenProvider;

	public CmmnAuthenticationSuccessHandler(CmmnUserDetailsService cmmnUserDetailsService, CmmnSecureTokenProvider cmmnSecureTokenProvider) {
		this.cmmnUserDetailsService = cmmnUserDetailsService;
		this.cmmnSecureTokenProvider = cmmnSecureTokenProvider;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		// 세션 설정
		HttpSession session = request.getSession(true);

		if (authentication.isAuthenticated()) {
			try {
				String secureToken = cmmnSecureTokenProvider.generateSecureToken(authentication, 15 * 60); // 토큰 생성 (15분)
				if (cmmnSecureTokenProvider.verifySecureToken(secureToken)) {
					HttpSessionRequestCache cache = new HttpSessionRequestCache();
					SavedRequest savedRequest = cache.getRequest(request, response);

					if (authentication.isAuthenticated()) {
						session.setAttribute("SESSION-X-AUTH-TOKEN", secureToken);
						session.setAttribute("SESSION_USER_IP", request.getRemoteAddr());
						session.setAttribute("SESSION_USER_AGENT", request.getHeader("User-Agent"));
						session.setAttribute("SESSION_LOGIN_TIME", System.currentTimeMillis());
					}

					response.setHeader("X-Auth-Token", secureToken);
					response.setHeader("tokenType", CmmnRFC6750.BEARER.getValue());

					if (savedRequest != null) {
						String targetUrl = savedRequest.getRedirectUrl();
						if (StringUtils.isNotBlank(targetUrl) && !targetUrl.contains("/login")) {
							cache.removeRequest(request, response);
							response.sendRedirect(targetUrl);
						}
					} else {
						response.sendRedirect(request.getContextPath() + "/");
					}
				}
			} catch (ParseException | JOSEException e) {
				throw new RuntimeException(e);
			}
		}
	}
}