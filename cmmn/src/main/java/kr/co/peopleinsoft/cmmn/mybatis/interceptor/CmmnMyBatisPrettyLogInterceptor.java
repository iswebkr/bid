package kr.co.peopleinsoft.cmmn.mybatis.interceptor;

import kr.co.peopleinsoft.cmmn.mybatis.handler.CmmnSlowQueryHandler;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * MyBatis SQL 쿼리 로깅 Interceptor
 */
@Getter
@Setter
@Intercepts({
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class CmmnMyBatisPrettyLogInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(CmmnMyBatisPrettyLogInterceptor.class);

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); // 날짜 포맷

	private double slowQueryThresholdSeconds = 3.0; // 느린 쿼리 기준 시간 (초)

	private CmmnSlowQueryHandler cmmnSlowQueryHandler;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		boolean isInfoEnabled = logger.isInfoEnabled();
		boolean isErrorEnabled = logger.isErrorEnabled();

		if (!isInfoEnabled && !isErrorEnabled) {
			return invocation.proceed();
		}

		long startTime = System.currentTimeMillis();
		QueryExecutionInfo queryInfo = new QueryExecutionInfo();
		queryInfo.setStartTime(startTime);

		Object result;

		try {
			// Executor 타입 감지
			Executor executor = (Executor) invocation.getTarget();
			String executorType = detectExecutorType(executor);

			// 쿼리 정보 추출
			extractQueryInfo(invocation, queryInfo, executorType, executor);

			// 쿼리 실행
			result = invocation.proceed();

			// 실행 시간 계산
			long endTime = System.currentTimeMillis();
			queryInfo.setEndTime(endTime);
			long executionTimeMillis = endTime - startTime;
			double executionTimeSeconds = executionTimeMillis / 1000.0;
			queryInfo.setExecutionTimeMillis(executionTimeMillis);
			queryInfo.setExecutionTimeSeconds(executionTimeSeconds);

			// 로그 출력
			if (isInfoEnabled) {
				printQueryLog(queryInfo);
			}

			return result;
		} catch (Throwable e) {
			// 실행 시간 계산
			long endTime = System.currentTimeMillis();
			queryInfo.setEndTime(endTime);
			long executionTimeMillis = endTime - startTime;
			double executionTimeSeconds = executionTimeMillis / 1000.0;
			queryInfo.setExecutionTimeMillis(executionTimeMillis);
			queryInfo.setExecutionTimeSeconds(executionTimeSeconds);

			// 에러 로그 출력
			if (isErrorEnabled) {
				printErrorLog(queryInfo, e);
			}

			throw e;
		}
	}

	/**
	 * Executor 타입 감지
	 */
	private String detectExecutorType(Executor executor) {
		try {
			Executor actualExecutor = executor;
			String executorClassName = executor.getClass().getSimpleName();

			// CachingExecutor인 경우 delegate 필드에서 실제 Executor 가져오기
			if (executorClassName.contains("Caching")) {
				try {
					Field delegateField = executor.getClass().getDeclaredField("delegate");
					delegateField.setAccessible(true);
					actualExecutor = (Executor) delegateField.get(executor);
					executorClassName = actualExecutor.getClass().getSimpleName();
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.debug("CachingExecutor의 delegate 필드 접근 실패", e);
					}
				}
			}

			// Executor 타입 판별
			if (executorClassName.contains("Batch") || executorClassName.equals("BatchExecutor")) {
				return "BATCH";
			} else if (executorClassName.contains("Reuse") || executorClassName.equals("ReuseExecutor")) {
				return "REUSE";
			} else if (executorClassName.contains("Simple") || executorClassName.equals("SimpleExecutor")) {
				return "SIMPLE";
			} else {
				// 알 수 없는 경우 클래스명 반환
				return executorClassName.toUpperCase();
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("ExecutorType 감지 실패", e);
			}

			return "UNKNOWN";
		}
	}

	/**
	 * 쿼리 정보 추출
	 */
	private void extractQueryInfo(Invocation invocation, QueryExecutionInfo queryInfo, String executorType, Executor executor) throws Exception {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		Object parameterObject = invocation.getArgs()[1];
		Configuration configuration = mappedStatement.getConfiguration();
		BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);

		// 기본 정보 설정
		queryInfo.setMethodName(invocation.getMethod().getName());
		queryInfo.setResourceFileName(extractResourceFileName(mappedStatement.getResource()));
		queryInfo.setQueryId(mappedStatement.getId());
		queryInfo.setNamespace(extractNamespace(mappedStatement.getId()));
		queryInfo.setSqlCommandType(mappedStatement.getSqlCommandType().name());
		queryInfo.setStatementType(mappedStatement.getStatementType().name());
		queryInfo.setExecutorType(executorType);

		// 데이터베이스 정보 추출
		Connection connection = executor.getTransaction().getConnection();
		DatabaseInfo dbInfo = extractDatabaseInfo(connection);
		queryInfo.setDatabaseInfo(dbInfo);

		// 파라미터 정보 추출
		List<ParameterInfo> parameters = extractParameters(boundSql, parameterObject, configuration);
		queryInfo.setParameters(parameters);

		// SQL 정보 설정 (파라미터 치환 및 포맷팅)
		String executedSql = buildExecutedSql(boundSql, parameterObject, configuration);
		queryInfo.setExecutedSql(executedSql);
	}

	/**
	 * 데이터베이스 정보 추출
	 */
	private DatabaseInfo extractDatabaseInfo(Connection connection) {
		DatabaseInfo dbInfo = new DatabaseInfo();

		try {
			DatabaseMetaData metaData = connection.getMetaData();

			dbInfo.setProductName(metaData.getDatabaseProductName());
			dbInfo.setProductVersion(metaData.getDatabaseProductVersion());
			dbInfo.setDriverName(metaData.getDriverName());
			dbInfo.setDriverVersion(metaData.getDriverVersion());
			dbInfo.setUrl(metaData.getURL());
			dbInfo.setUserName(metaData.getUserName());

			// 스키마 정보 (데이터베이스에 따라 지원 여부가 다름)
			try {
				dbInfo.setSchema(connection.getSchema());
			} catch (Exception e) {
				dbInfo.setSchema("N/A");
			}

			// 카탈로그 정보
			try {
				dbInfo.setCatalog(connection.getCatalog());
			} catch (Exception e) {
				dbInfo.setCatalog("N/A");
			}

		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.warn("데이터베이스 정보 추출 중 에러 발생", e);
			}
		}

		return dbInfo;
	}

	/**
	 * 파라미터 정보 추출
	 */
	private List<ParameterInfo> extractParameters(BoundSql boundSql, Object parameterObject, Configuration configuration) {
		List<ParameterInfo> parameters = new ArrayList<>();
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

		if (parameterMappings != null) {
			for (ParameterMapping parameterMapping : parameterMappings) {
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					String propertyName = parameterMapping.getProperty();
					Object value;

					if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (parameterObject == null) {
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else {
						MetaObject metaObject = configuration.newMetaObject(parameterObject);
						value = metaObject.getValue(propertyName);
					}

					String typeName = value != null ? value.getClass().getSimpleName() : "null";
					String formattedValue = formatValue(value);

					parameters.add(new ParameterInfo(propertyName, typeName, value, formattedValue));
				}
			}
		}

		return parameters;
	}

	/**
	 * 실행 SQL 구성 (파라미터 치환, 포맷팅)
	 */
	private String buildExecutedSql(BoundSql boundSql, Object parameterObject, Configuration configuration) {
		// 1. SQL 가져오기
		String sql = boundSql.getSql();

		// 2. 파라미터 치환
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

		if (parameterMappings != null && !parameterMappings.isEmpty()) {
			for (ParameterMapping parameterMapping : parameterMappings) {
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					Object value;
					String propertyName = parameterMapping.getProperty();

					if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (parameterObject == null) {
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else {
						MetaObject metaObject = configuration.newMetaObject(parameterObject);
						value = metaObject.getValue(propertyName);
					}

					sql = replacePlaceholder(sql, value);
				}
			}
		}

		return sql;
	}

	/**
	 * 키워드 매칭 확인 (정확한 단어 경계 확인)
	 */
	private boolean startsWithKeyword(String sql, int pos, String keyword) {
		if (pos + keyword.length() > sql.length()) {
			return false;
		}

		// 키워드와 매칭되는지 확인
		String substring = sql.substring(pos, pos + keyword.length());
		if (!substring.equals(keyword)) {
			return false;
		}

		// 이전 문자가 공백이거나 시작 위치인지 확인
		if (pos > 0) {
			char prevChar = sql.charAt(pos - 1);
			return Character.isWhitespace(prevChar) || prevChar == '(' || prevChar == ')';
		}

		return true;
	}

	/**
	 * SQL의 ? 를 실제 값으로 치환
	 */
	private String replacePlaceholder(String sql, Object value) {
		String formattedValue = formatValue(value);
		return sql.replaceFirst("\\?", Matcher.quoteReplacement(formattedValue));
	}

	/**
	 * 값 포맷팅
	 */
	private String formatValue(Object value) {
		return switch (value) {
			case null -> "null";
			case String s -> "'" + value + "'";
			case java.sql.Date date -> "'" + value + "'";
			case java.sql.Timestamp timestamp -> "'" + DATE_FORMAT.format(timestamp) + "'";
			case Date date -> "'" + DATE_FORMAT.format(date) + "'";
			default -> value.toString();
		};
	}

	/**
	 * 쿼리 로그 출력
	 */
	private void printQueryLog(QueryExecutionInfo queryInfo) {
		StringBuilder log = new StringBuilder("\n");

		// ExecutorType에 따른 타이틀 표시
		String executorType = queryInfo.getExecutorType();
		String executorIcon = getExecutorIcon(executorType);

		log.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
		log.append("┃ MyBatis Query Execution Log ").append(executorIcon).append("\n");
		log.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫\n");

		// 데이터베이스 정보
		DatabaseInfo dbInfo = queryInfo.getDatabaseInfo();
		if (dbInfo != null) {
			log.append("┃ [DATABASE INFORMATION]\n");
			log.append("┃ Product Name    : ").append(dbInfo.getProductName()).append("\n");
			log.append("┃ Product Version : ").append(dbInfo.getProductVersion()).append("\n");
			log.append("┃ Driver Name     : ").append(dbInfo.getDriverName()).append("\n");
			log.append("┃ Driver Version  : ").append(dbInfo.getDriverVersion()).append("\n");
			log.append("┃ URL             : ").append(dbInfo.getUrl()).append("\n");
			log.append("┃ User Name       : ").append(dbInfo.getUserName()).append("\n");
			log.append("┃ Schema          : ").append(dbInfo.getSchema()).append("\n");
			log.append("┃ Catalog         : ").append(dbInfo.getCatalog()).append("\n");
			log.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫\n");
		}

		// 매퍼 정보
		log.append("┃ [MAPPER INFORMATION]\n");
		log.append("┃ Resource File   : ").append(queryInfo.getResourceFileName()).append("\n");
		log.append("┃ Namespace       : ").append(queryInfo.getNamespace()).append("\n");
		log.append("┃ Query ID        : ").append(queryInfo.getQueryId()).append("\n");
		log.append("┃ Method Name     : ").append(queryInfo.getMethodName()).append("\n");
		log.append("┃ Command Type    : ").append(queryInfo.getSqlCommandType()).append("\n");
		log.append("┃ Statement Type  : ").append(queryInfo.getStatementType()).append("\n");
		log.append("┃ Executor Type   : ").append(executorType).append("\n");
		log.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫\n");

		// 파라미터 정보
		List<ParameterInfo> parameters = queryInfo.getParameters();
		if (parameters != null && !parameters.isEmpty()) {
			log.append("┃ [PARAMETERS]\n");
			for (int i = 0; i < parameters.size(); i++) {
				ParameterInfo param = parameters.get(i);
				log.append(String.format("┃ [%d] %s (%s) : %s\n",
					i + 1,
					param.getName(),
					param.getType(),
					param.getFormattedValue()));
			}
			log.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫\n");
		}

		log.append("┃ [EXECUTED SQL]\n");
		log.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n");
		String[] sqlLines = queryInfo.getExecutedSql().split("\n");
		for (String line : sqlLines) {
			log.append("  ").append(line).append("\n");
		}
		log.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
		log.append("┃ [EXECUTION TIME]\n");
		log.append(String.format("┃ Execution Time  : %.3f seconds (%d ms)",
			queryInfo.getExecutionTimeSeconds(),
			queryInfo.getExecutionTimeMillis()));

		if (queryInfo.getExecutionTimeSeconds() >= slowQueryThresholdSeconds) {
			// 느린 쿼리를 처리하기 위한 핸들러가 지정된 경우 해당 핸들러 에게 위임 (delegate)
			if(getCmmnSlowQueryHandler() != null) {
				getCmmnSlowQueryHandler().handle(queryInfo);
			}
			log.append("⚠️ SLOW QUERY DETECTED!");
		}
		log.append("\n");
		log.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");

		if (logger.isInfoEnabled()) {
			logger.info(log.toString());
		}
	}

	/**
	 * 에러 로그 출력
	 */
	private void printErrorLog(QueryExecutionInfo queryInfo, Throwable e) {
		StringBuilder log = new StringBuilder("\n");

		String executorType = queryInfo.getExecutorType();

		log.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
		log.append("┃ ❌ MyBatis Query Execution Error\n");
		log.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫\n");
		log.append("┃ [ERROR INFORMATION]\n");
		log.append("┃ Exception Type  : ").append(e.getClass().getName()).append("\n");
		log.append("┃ Error Message   : ").append(e.getMessage()).append("\n");
		log.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫\n");
		log.append("┃ [MAPPER INFORMATION]\n");
		log.append("┃ Query ID        : ").append(queryInfo.getQueryId()).append("\n");
		log.append("┃ Resource File   : ").append(queryInfo.getResourceFileName()).append("\n");
		log.append("┃ Executor Type   : ").append(executorType).append("\n");
		log.append("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫\n");
		log.append("┃ [EXECUTED SQL]\n");
		log.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n");
		String[] sqlLines = queryInfo.getExecutedSql().split("\n");
		for (String line : sqlLines) {
			log.append("  ").append(line).append("\n");
		}
		log.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
		log.append("┃ [EXECUTION TIME]\n");
		log.append(String.format("┃ Execution Time  : %.3f seconds (%d ms)\n",
			queryInfo.getExecutionTimeSeconds(),
			queryInfo.getExecutionTimeMillis()));
		log.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");

		if (logger.isErrorEnabled()) {
			logger.error(log.toString(), e);
		}
	}

	/**
	 * ExecutorType에 따른 아이콘 반환
	 */
	private String getExecutorIcon(String executorType) {
		return switch (executorType) {
			case "BATCH" -> "🔄 [BATCH]";
			case "REUSE" -> "♻️ [REUSE]";
			case "SIMPLE" -> "⚡ [SIMPLE]";
			default -> "[" + executorType + "]";
		};
	}

	/**
	 * 리소스 파일명 추출
	 */
	private String extractResourceFileName(String resourcePath) {
		if (resourcePath == null || resourcePath.isEmpty()) {
			return "N/A";
		}

		int lastSlashIndex = resourcePath.lastIndexOf("/");
		int lastBracketIndex = resourcePath.lastIndexOf("]");

		if (lastSlashIndex > 0 && lastBracketIndex > lastSlashIndex) {
			return resourcePath.substring(lastSlashIndex + 1, lastBracketIndex);
		}

		return resourcePath;
	}

	/**
	 * Namespace 추출
	 */
	private String extractNamespace(String queryId) {
		if (queryId == null || queryId.isEmpty()) {
			return "N/A";
		}

		int lastDotIndex = queryId.lastIndexOf(".");
		if (lastDotIndex > 0) {
			return queryId.substring(0, lastDotIndex);
		}

		return queryId;
	}

	// Getter and Setter

	@Setter
	@Getter
	public static class QueryExecutionInfo {
		// Getters and Setters
		private DatabaseInfo databaseInfo;
		private String resourceFileName;
		private String namespace;
		private String queryId;
		private String sqlCommandType;
		private String statementType;
		private String methodName;
		private String executorType;
		private List<ParameterInfo> parameters;
		private String executedSql;
		private double executionTimeSeconds;
		private long executionTimeMillis;
		private long startTime;
		private long endTime;
	}

	@Setter
	@Getter
	public static class DatabaseInfo {
		// Getters and Setters
		private String productName;
		private String productVersion;
		private String driverName;
		private String driverVersion;
		private String url;
		private String userName;
		private String schema;
		private String catalog;
	}

	@Setter
	@Getter
	public static class ParameterInfo {
		// Getters and Setters
		private String name;
		private String type;
		private Object value;
		private String formattedValue;

		public ParameterInfo(String name, String type, Object value, String formattedValue) {
			this.name = name;
			this.type = type;
			this.value = value;
			this.formattedValue = formattedValue;
		}
	}
}