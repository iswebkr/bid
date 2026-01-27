package kr.co.peopleinsoft.cmmn.filter.html;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmmnHtmlTagFilter implements Filter {

	List<String> excludePatternList;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String excludePattenrs = filterConfig.getInitParameter("excludePatterns");
		excludePatternList = new ArrayList<>();
		if (excludePattenrs != null) {
			excludePatternList.addAll(Arrays.asList(excludePattenrs.split(",")));
		}
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		if (!excludePatternList.isEmpty()) {
			for (String pattern : excludePatternList) {
				if (request.getRequestURI().matches(pattern)) {
					filterChain.doFilter(servletRequest, servletResponse);
					break;
				} else {
					filterChain.doFilter(new CmmnHttpServletRequestWrapper((HttpServletRequest) servletRequest), servletResponse);
					break;
				}
			}
		} else {
			filterChain.doFilter(new CmmnHttpServletRequestWrapper((HttpServletRequest) servletRequest), servletResponse);
		}
	}
}