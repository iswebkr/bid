package kr.co.peopleinsoft.cmmn.mybatis;

import kr.co.peopleinsoft.biz.mapper.CmmnMapper;
import kr.co.peopleinsoft.cmmn.mybatis.handler.CmmnMyBatisClobTypeHandler;
import kr.co.peopleinsoft.cmmn.mybatis.handler.impl.CmmnSimpleQueryHandler;
import kr.co.peopleinsoft.cmmn.mybatis.interceptor.CmmnMyBatisCamelCaseInterceptor;
import kr.co.peopleinsoft.cmmn.mybatis.interceptor.CmmnMyBatisPrettyLogInterceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Configuration
public class CmmnMyBatisConfig {

	static final Logger logger = LoggerFactory.getLogger(CmmnMyBatisConfig.class);

	final DataSource dataSource;
	final ConfigurableApplicationContext applicationContext;

	public CmmnMyBatisConfig(DataSource dataSource, ConfigurableApplicationContext applicationContext) {
		this.dataSource = dataSource;
		this.applicationContext = applicationContext;
	}

	/**
	 * MyBatis Configuration
	 */
	@Bean
	org.apache.ibatis.session.Configuration mybatisConfiguration() {
		// 쿼리 로그 Interceptor 설정
		CmmnMyBatisPrettyLogInterceptor myBatisPrettyLogInterceptor = new CmmnMyBatisPrettyLogInterceptor();
		myBatisPrettyLogInterceptor.setSlowQueryThresholdSeconds(3.0);
		myBatisPrettyLogInterceptor.setCmmnSlowQueryHandler(new CmmnSimpleQueryHandler());

		org.apache.ibatis.session.Configuration mybatisConfiguration = new org.apache.ibatis.session.Configuration();
		mybatisConfiguration.setUseGeneratedKeys(false);
		mybatisConfiguration.setMapUnderscoreToCamelCase(true);
		mybatisConfiguration.setJdbcTypeForNull(JdbcType.NULL);
		mybatisConfiguration.addInterceptor(myBatisPrettyLogInterceptor);
		mybatisConfiguration.addInterceptor(new CmmnMyBatisCamelCaseInterceptor());
		mybatisConfiguration.getTypeHandlerRegistry().register(CmmnMyBatisClobTypeHandler.class);
		return mybatisConfiguration;
	}

	/**
	 * MyBatis SqlSessionFactory
	 */
	@Bean
	SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setConfiguration(mybatisConfiguration());
		sessionFactory.setTransactionFactory(new SpringManagedTransactionFactory());
		sessionFactory.setMapperLocations(applicationContext.getResources("classpath*:**/*.sql.xml"));
		sessionFactory.setDatabaseIdProvider(dbSource -> {
			if (dbSource != null) {
				try (Connection connection = dbSource.getConnection()) {
					DatabaseMetaData metaData = connection.getMetaData();
					String databaseName = metaData.getDatabaseProductName();
					if (databaseName.toLowerCase().contains("oracle")) {
						return "oracle";
					} else if (databaseName.toLowerCase().contains("mysql")) {
						return "mysql";
					} else if (databaseName.toLowerCase().contains("postgresql")) {
						return "postgresql";
					} else if (databaseName.toLowerCase().contains("h2")) {
						return "h2";
					} else if (databaseName.toLowerCase().contains("microsoft")) {
						return "sqlserver";
					} else if (databaseName.toLowerCase().contains("mariadb")) {
						return "mariadb";
					} else if (databaseName.toLowerCase().contains("tibero")) {
						return "tibero";
					}
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage());
				}
			}
			return null;
		});
		return sessionFactory.getObject();
	}

	/**
	 * Default SqlSessionTemplate (ExecutorType : SIMPLE)
	 */
	@Bean
	SqlSessionTemplate sqlSessionTemplate() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory(), ExecutorType.SIMPLE);
	}

	/**
	 * Batch 처리용 SqlSessionTemplate (ExecutorType : BATCH)
	 */
	@Bean
	SqlSessionTemplate batchSqlSessionTemplate() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory(), ExecutorType.BATCH);
	}

	/**
	 * 재사용 쿼리용 SqlSessionTemplate (ExecutorType : REUSE)
	 */
	@Bean
	SqlSessionTemplate reuseSqlSessionTemplate() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory(), ExecutorType.REUSE);
	}

	/**
	 * CmmnMapper 에서 위에서 선언한 각 상황별 SqlSessionTemplate 를 사용 가능하도록 전달
	 */
	@Bean
	CmmnMapper cmmnMapper() throws Exception {
		return new CmmnMapper(sqlSessionTemplate(), batchSqlSessionTemplate(), reuseSqlSessionTemplate());
	}
}