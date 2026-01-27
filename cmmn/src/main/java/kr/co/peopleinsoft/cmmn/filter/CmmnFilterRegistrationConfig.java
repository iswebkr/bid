package kr.co.peopleinsoft.cmmn.filter;

import jakarta.servlet.DispatcherType;
import kr.co.peopleinsoft.cmmn.filter.html.CmmnHtmlTagFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CmmnFilterRegistrationConfig {
	@Bean
	FilterRegistrationBean<?> registerHtmlTagFilter() {
		var registration = new FilterRegistrationBean<>(new CmmnHtmlTagFilter());
		Map<String, String> initParams = new HashMap<>();
		initParams.put("excludePatterns", "/h2/console/*, /swagger-ui/*");

		registration.setName("HtmlTagFilter");
		registration.setAsyncSupported(true);
		registration.addUrlPatterns("/*");
		registration.setInitParameters(initParams);
		registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));

		return registration;
	}
}