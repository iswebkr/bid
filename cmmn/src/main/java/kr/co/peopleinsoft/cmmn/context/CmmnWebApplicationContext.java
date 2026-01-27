package kr.co.peopleinsoft.cmmn.context;

import kr.co.peopleinsoft.cmmn.datasource.router.DataSourceContextHolder;
import kr.co.peopleinsoft.cmmn.datasource.router.DataSourceType;
import kr.co.peopleinsoft.cmmn.message.CmmnReloadableResourceBundleMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.converter.xml.JacksonXmlHttpMessageConverter;
import org.springframework.http.converter.yaml.JacksonYamlHttpMessageConverter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.json.JacksonJsonView;
import org.springframework.web.servlet.view.xml.JacksonXmlView;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Configuration
public class CmmnWebApplicationContext implements WebMvcConfigurer {

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout((1000 * 60) * 10).setTaskExecutor(asyncTaskExecutor());
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/main");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		BeanNameViewResolver beanNameView = new BeanNameViewResolver();
		beanNameView.setOrder(1);

		InternalResourceViewResolver internalResourceView = new InternalResourceViewResolver();
		internalResourceView.setViewClass(JstlView.class);
		internalResourceView.setPrefix("/WEB-INF/views/");
		internalResourceView.setSuffix(".jsp");
		internalResourceView.setOrder(2);

		JacksonJsonView jsonView = new JacksonJsonView();
		jsonView.setExtractValueFromSingleKeyModel(true);

		JacksonXmlView xmlView = new JacksonXmlView();

		registry.viewResolver(beanNameView);
		registry.viewResolver(internalResourceView);
		registry.enableContentNegotiation(jsonView, xmlView);
		registry.jsp("/WEB-INF/jsp", ".jsp");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/**");
	}

	@Override
	public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
		stringHttpMessageConverter.setSupportedMediaTypes(List.of(
			MediaType.TEXT_HTML,
			MediaType.TEXT_PLAIN,
			MediaType.ALL
		));

		JsonMapper jsonMapper = JsonMapper.builder()
			.findAndAddModules()
			.enable(SerializationFeature.INDENT_OUTPUT)
			.defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
			.build();

		XmlMapper xmlMapper = XmlMapper.xmlBuilder()
			.findAndAddModules()
			.enable(SerializationFeature.INDENT_OUTPUT)
			.defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
			.build();

		YAMLMapper yamlMapper = YAMLMapper.builder()
			.findAndAddModules()
			.enable(SerializationFeature.INDENT_OUTPUT)
			.defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
			.build();

		JacksonJsonHttpMessageConverter jacksonJsonHttpMessageConverter = new JacksonJsonHttpMessageConverter(jsonMapper);
		jacksonJsonHttpMessageConverter.setSupportedMediaTypes(List.of(MediaType.TEXT_HTML, MediaType.APPLICATION_JSON));

		JacksonXmlHttpMessageConverter jacksonXmlHttpMessageConverter = new JacksonXmlHttpMessageConverter(xmlMapper);
		jacksonXmlHttpMessageConverter.setSupportedMediaTypes(List.of(MediaType.TEXT_XML, MediaType.TEXT_HTML, MediaType.APPLICATION_XML));

		JacksonYamlHttpMessageConverter jacksonYamlHttpMessageConverter = new JacksonYamlHttpMessageConverter(yamlMapper);
		jacksonYamlHttpMessageConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_YAML));

		builder
			.withStringConverter(stringHttpMessageConverter)
			.withJsonConverter(jacksonJsonHttpMessageConverter)
			.withXmlConverter(jacksonXmlHttpMessageConverter)
			.withYamlConverter(jacksonYamlHttpMessageConverter)
			.build();
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 모든 경로에 대해
			.allowCredentials(true) // 쿠키/인증정보 전송 허용
			.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 허용할 Method
			.allowedOriginPatterns("*") // 허용할 오리진(출처) 목록
			.allowedHeaders("*") // 허용할 http header
			.maxAge(5000L);
	}

	@Bean
	AsyncTaskExecutor asyncTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(10);
		taskExecutor.setMaxPoolSize(50);
		taskExecutor.setQueueCapacity(100);
		taskExecutor.setThreadNamePrefix("app-thread-async");
		taskExecutor.setTaskDecorator(runnable -> {
			DataSourceType dataSourceType = DataSourceContextHolder.getDataSourceType();
			return () -> {
				try {
					DataSourceContextHolder.setDataSourceType(dataSourceType);
					runnable.run();
				} finally {
					DataSourceContextHolder.clearDataSourceType();
				}
			};
		});
		taskExecutor.initialize();
		return taskExecutor;
	}

	@Bean
	LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}

	@Bean
	MessageSource messageSource(NamedParameterJdbcTemplate jdbcTemplate) {
		ReloadableResourceBundleMessageSource messageSource = new CmmnReloadableResourceBundleMessageSource(jdbcTemplate);
		messageSource.setBasenames("classpath:/i18n/messages");
		messageSource.setCacheSeconds(60);

		messageSource.setFallbackToSystemLocale(false);
		messageSource.setDefaultLocale(Locale.KOREA);
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setAlwaysUseMessageFormat(true);
		return messageSource;
	}
}