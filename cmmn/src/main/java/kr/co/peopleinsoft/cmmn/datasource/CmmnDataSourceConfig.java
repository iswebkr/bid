package kr.co.peopleinsoft.cmmn.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CmmnDataSourceConfig {

	final ConfigurableEnvironment environment;

	public CmmnDataSourceConfig(ConfigurableEnvironment environment) {
		this.environment = environment;
	}

	@Bean
	NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	/*** 라우팅 데이터 소스 ***/
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	DataSource routingDataSource(@Qualifier("defaultDataSource") DataSource defaultDataSource) {
		CmmnRoutingDataSource routingDataSource = new CmmnRoutingDataSource();

		Map<Object, Object> dataSourceMap = new HashMap<>();
		dataSourceMap.put("defaultDataSource", defaultDataSource);

		routingDataSource.setTargetDataSources(dataSourceMap);
		routingDataSource.setDefaultTargetDataSource(defaultDataSource);
		routingDataSource.afterPropertiesSet();

		return routingDataSource;
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
		return new LazyConnectionDataSourceProxy(routingDataSource);
	}

	/*** 벤더별 DataSource 는 아래에 추가 ***/
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.default")
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	DataSource defaultDataSource() {
		return DataSourceBuilder.create().build();
	}
}