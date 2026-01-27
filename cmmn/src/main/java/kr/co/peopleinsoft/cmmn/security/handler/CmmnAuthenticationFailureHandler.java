package kr.co.peopleinsoft.cmmn.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.ContentType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class CmmnAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		ObjectMapper mapper = new ObjectMapper();
		response.setContentType(ContentType.TEXT_HTML.getMimeType());
		response.getWriter().write(mapper.writeValueAsString("에러 : " + exception.getMessage()));
	}
}