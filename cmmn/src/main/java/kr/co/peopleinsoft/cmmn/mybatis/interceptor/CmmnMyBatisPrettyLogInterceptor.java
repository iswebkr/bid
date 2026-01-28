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
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * MyBatis SQL 쿼리 로깅 Interceptor (완전판)
 * 주요 기능:
 * - 주석과 문자열 리터럴을 정확히 구분한 파라미터 치환
 * - 다양한 데이터베이스별 문자열 리터럴 지원 (Oracle q'[]', PostgreSQL $$, MySQL backtick)
 * - 이스케이프 문자 처리 (\')
 * - SQL 포맷팅 옵션
 * - 느린 쿼리 감지 및 핸들러 연동
 */
@Getter
@Setter
@Intercepts({
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class CmmnMyBatisPrettyLogInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(CmmnMyBatisPrettyLogInterceptor.class);

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	// SQL 키워드 목록 (포맷팅용)
	private static final Set<String> SQL_KEYWORDS = new HashSet<>(Arrays.asList(
		"SELECT", "FROM", "WHERE", "AND", "OR", "ORDER BY", "GROUP BY", "HAVING",
		"INSERT", "INTO", "VALUES", "UPDATE", "SET", "DELETE", "JOIN", "LEFT JOIN",
		"RIGHT JOIN", "INNER JOIN", "OUTER JOIN", "ON", "AS", "DISTINCT", "UNION",
		"CASE", "WHEN", "THEN", "ELSE", "END", "IN", "NOT IN", "EXISTS", "NOT EXISTS",
		"BETWEEN", "LIKE", "IS NULL", "IS NOT NULL", "LIMIT", "OFFSET"
	));

	// 설정 옵션
	private double slowQueryThresholdSeconds = 3.0; // 느린 쿼리 기준 시간 (초)
	private boolean prettifySQL = true; // SQL 포맷팅 여부
	private boolean uppercaseKeywords = false; // SQL 키워드 대문자 변환 여부
	private int indentSize = 4; // SQL 들여쓰기 크기
	private boolean includeStackTrace = true; // 에러 시 스택 트레이스 포함 여부

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
					if (logger.isDebugEnabled()) {
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
		queryInfo.setExecutedSql(boundSql.getSql()); // 매핑전 쿼리를 미리 등록 (exception 발생 시 해당 쿼리를 출력)

		// 데이터베이스 정보 추출
		Connection connection = executor.getTransaction().getConnection();
		DatabaseInfo dbInfo = extractDatabaseInfo(connection);
		queryInfo.setDatabaseInfo(dbInfo);

		// 파라미터 정보 추출
		List<ParameterInfo> parameters = extractParameters(boundSql, parameterObject, configuration);
		queryInfo.setParameters(parameters);

		// SQL 정보 설정 (파라미터 치환 및 포맷팅)
		String executedSql = buildExecutedSql(boundSql, parameterObject, configuration, dbInfo);
		queryInfo.setExecutedSql(executedSql);
	}

	/**
	 * 데이터베이스 정보 추출
	 */
	private DatabaseInfo extractDatabaseInfo(Connection connection) {
		DatabaseInfo dbInfo = new DatabaseInfo();

		try {
			DatabaseMetaData metaData = connection.getMetaData();

			String productName = metaData.getDatabaseProductName();
			dbInfo.setProductName(productName);
			dbInfo.setProductVersion(metaData.getDatabaseProductVersion());
			dbInfo.setDriverName(metaData.getDriverName());
			dbInfo.setDriverVersion(metaData.getDriverVersion());
			dbInfo.setUrl(metaData.getURL());
			dbInfo.setUserName(metaData.getUserName());

			// 데이터베이스 타입 판별
			dbInfo.setDatabaseType(detectDatabaseType(productName));

			// 스키마 정보
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
			if (logger.isWarnEnabled()) {
				logger.warn("데이터베이스 정보 추출 중 에러 발생", e);
			}
		}

		return dbInfo;
	}

	/**
	 * 데이터베이스 타입 판별
	 */
	private DatabaseType detectDatabaseType(String productName) {
		if (productName == null) {
			return DatabaseType.UNKNOWN;
		}

		String upperProductName = productName.toUpperCase();

		if (upperProductName.contains("ORACLE")) {
			return DatabaseType.ORACLE;
		} else if (upperProductName.contains("MYSQL")) {
			return DatabaseType.MYSQL;
		} else if (upperProductName.contains("MARIADB")) {
			return DatabaseType.MARIADB;
		} else if (upperProductName.contains("POSTGRESQL")) {
			return DatabaseType.POSTGRESQL;
		} else if (upperProductName.contains("TIBERO")) {
			return DatabaseType.TIBERO;
		} else if (upperProductName.contains("MSSQL") || upperProductName.contains("SQL SERVER")) {
			return DatabaseType.MSSQL;
		} else if (upperProductName.contains("H2")) {
			return DatabaseType.H2;
		} else if (upperProductName.contains("HSQL")) {
			return DatabaseType.HSQLDB;
		} else if (upperProductName.contains("DERBY")) {
			return DatabaseType.DERBY;
		} else {
			return DatabaseType.UNKNOWN;
		}
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
	private String buildExecutedSql(BoundSql boundSql, Object parameterObject, Configuration configuration, DatabaseInfo dbInfo) {
		// 1. SQL 가져오기
		String sql = boundSql.getSql();

		// 2. 파라미터 값 목록 생성
		List<String> parameterValues = new ArrayList<>();
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
					parameterValues.add(formatValue(value));
				}
			}
		}

		// 3. SQL 파싱 및 파라미터 치환
		String executedSql = replaceParametersInSql(sql, parameterValues, dbInfo);

		// 4. SQL 포맷팅 (옵션에 따라)
		if (prettifySQL) {
			executedSql = formatSql(executedSql);
		}

		return executedSql;
	}

	/**
	 * SQL 파싱 상태 Enum
	 */
	private enum SqlParseState {
		NORMAL,                    // 일반 SQL 영역
		IN_SINGLE_QUOTE,          // 작은따옴표 문자열 리터럴 내부 (')
		IN_DOUBLE_QUOTE,          // 큰따옴표 문자열 리터럴 내부 (")
		IN_BACKTICK,              // 백틱 식별자 내부 (`) - MySQL
		IN_LINE_COMMENT,          // 한 줄 주석 내부 (--)
		IN_BLOCK_COMMENT,         // 블록 주석 내부 (/* */)
		IN_ORACLE_QUOTE,          // Oracle q'[]' 형식 문자열
		IN_DOLLAR_QUOTE           // PostgreSQL $$ $$ 형식 문자열
	}

	/**
	 * SQL에서 주석과 문자열 리터럴을 구분하여 파라미터(?)만 치환
	 */
	private String replaceParametersInSql(String sql, List<String> parameterValues, DatabaseInfo dbInfo) {
		if (parameterValues == null || parameterValues.isEmpty()) {
			return sql;
		}

		StringBuilder result = new StringBuilder(sql.length() * 2);
		SqlParseState state = SqlParseState.NORMAL;
		int paramIndex = 0;
		int length = sql.length();

		// Oracle q'[]' 구문을 위한 변수
		char oracleQuoteDelimiter = '\0';

		// PostgreSQL $tag$ 구문을 위한 변수
		String dollarQuoteTag = null;

		for (int i = 0; i < length; i++) {
			char currentChar = sql.charAt(i);
			char nextChar = (i + 1 < length) ? sql.charAt(i + 1) : '\0';
			char prevChar = (i > 0) ? sql.charAt(i - 1) : '\0';

			switch (state) {
				case NORMAL:
					if (currentChar == '\'') {
						// Oracle q'[]' 구문 체크
						if (dbInfo != null && dbInfo.getDatabaseType() == DatabaseType.ORACLE && prevChar == 'q') {
							state = SqlParseState.IN_ORACLE_QUOTE;
							oracleQuoteDelimiter = nextChar;
							result.append(currentChar);
						} else {
							// 일반 작은따옴표 문자열 시작
							state = SqlParseState.IN_SINGLE_QUOTE;
							result.append(currentChar);
						}
					} else if (currentChar == '"') {
						// 큰따옴표 문자열 시작
						state = SqlParseState.IN_DOUBLE_QUOTE;
						result.append(currentChar);
					} else if (currentChar == '`' &&
						(dbInfo == null || dbInfo.getDatabaseType() == DatabaseType.MYSQL ||
							dbInfo.getDatabaseType() == DatabaseType.MARIADB)) {
						// MySQL/MariaDB 백틱 식별자 시작
						state = SqlParseState.IN_BACKTICK;
						result.append(currentChar);
					} else if (currentChar == '$' &&
						(dbInfo != null && dbInfo.getDatabaseType() == DatabaseType.POSTGRESQL)) {
						// PostgreSQL $tag$ 구문 체크
						String tag = extractDollarQuoteTag(sql, i);
						if (tag != null) {
							state = SqlParseState.IN_DOLLAR_QUOTE;
							dollarQuoteTag = tag;
							result.append(tag);
							i += tag.length() - 1; // 태그 길이만큼 건너뛰기
						} else {
							result.append(currentChar);
						}
					} else if (currentChar == '-' && nextChar == '-') {
						// 한 줄 주석 시작
						state = SqlParseState.IN_LINE_COMMENT;
						result.append(currentChar);
					} else if (currentChar == '/' && nextChar == '*') {
						// 블록 주석 시작
						state = SqlParseState.IN_BLOCK_COMMENT;
						result.append(currentChar);
					} else if (currentChar == '?') {
						// 파라미터 치환
						if (paramIndex < parameterValues.size()) {
							result.append(parameterValues.get(paramIndex));
							paramIndex++;
						} else {
							result.append(currentChar);
						}
					} else {
						result.append(currentChar);
					}
					break;

				case IN_SINGLE_QUOTE:
					result.append(currentChar);
					if (currentChar == '\\' && nextChar != '\0') {
						// 백슬래시 이스케이프 처리
						result.append(nextChar);
						i++; // 다음 문자 건너뛰기
					} else if (currentChar == '\'') {
						// 연속된 작은따옴표('')는 이스케이프된 작은따옴표
						if (nextChar == '\'') {
							result.append(nextChar);
							i++; // 다음 문자 건너뛰기
						} else {
							// 문자열 종료
							state = SqlParseState.NORMAL;
						}
					}
					break;

				case IN_DOUBLE_QUOTE:
					result.append(currentChar);
					if (currentChar == '\\' && nextChar != '\0') {
						// 백슬래시 이스케이프 처리
						result.append(nextChar);
						i++; // 다음 문자 건너뛰기
					} else if (currentChar == '"') {
						// 연속된 큰따옴표("")는 이스케이프된 큰따옴표
						if (nextChar == '"') {
							result.append(nextChar);
							i++; // 다음 문자 건너뛰기
						} else {
							// 문자열 종료
							state = SqlParseState.NORMAL;
						}
					}
					break;

				case IN_BACKTICK:
					result.append(currentChar);
					if (currentChar == '\\' && nextChar != '\0') {
						// 백슬래시 이스케이프 처리
						result.append(nextChar);
						i++; // 다음 문자 건너뛰기
					} else if (currentChar == '`') {
						// 백틱 종료
						state = SqlParseState.NORMAL;
					}
					break;

				case IN_ORACLE_QUOTE:
					result.append(currentChar);
					// Oracle q'[]' 구문의 종료 체크
					if (currentChar == getOracleQuoteClosingDelimiter(oracleQuoteDelimiter) &&
						nextChar == '\'') {
						result.append(nextChar);
						i++; // ' 문자 건너뛰기
						state = SqlParseState.NORMAL;
						oracleQuoteDelimiter = '\0';
					}
					break;

				case IN_DOLLAR_QUOTE:
					result.append(currentChar);
					// PostgreSQL $tag$ 구문의 종료 체크
					if (currentChar == '$' && dollarQuoteTag != null) {
						String endTag = extractDollarQuoteTag(sql, i);
						if (dollarQuoteTag.equals(endTag)) {
							result.append(endTag.substring(1)); // 첫 $ 제외하고 추가
							i += endTag.length() - 1;
							state = SqlParseState.NORMAL;
							dollarQuoteTag = null;
						}
					}
					break;

				case IN_LINE_COMMENT:
					result.append(currentChar);
					if (currentChar == '\n' || currentChar == '\r') {
						// 한 줄 주석 종료
						state = SqlParseState.NORMAL;
					}
					break;

				case IN_BLOCK_COMMENT:
					result.append(currentChar);
					if (currentChar == '*' && nextChar == '/') {
						// 블록 주석 종료
						result.append(nextChar);
						i++; // 다음 문자 건너뛰기
						state = SqlParseState.NORMAL;
					}
					break;
			}
		}

		return result.toString();
	}

	/**
	 * Oracle q'[]' 구문의 닫는 구분자 반환
	 */
	private char getOracleQuoteClosingDelimiter(char openingDelimiter) {
		return switch (openingDelimiter) {
			case '[' -> ']';
			case '(' -> ')';
			case '{' -> '}';
			case '<' -> '>';
			default -> openingDelimiter;
		};
	}

	/**
	 * PostgreSQL $tag$ 구문의 태그 추출
	 */
	private String extractDollarQuoteTag(String sql, int startPos) {
		StringBuilder tag = new StringBuilder("$");
		int pos = startPos + 1;
		int length = sql.length();

		while (pos < length) {
			char ch = sql.charAt(pos);
			if (ch == '$') {
				tag.append(ch);
				return tag.toString();
			} else if (Character.isLetterOrDigit(ch) || ch == '_') {
				tag.append(ch);
				pos++;
			} else {
				// 유효하지 않은 태그
				return null;
			}
		}

		return null;
	}

	/**
	 * SQL 포맷팅 (들여쓰기, 키워드 대문자화)
	 */
	private String formatSql(String sql) {
		if (sql == null || sql.trim().isEmpty()) {
			return sql;
		}

		StringBuilder formatted = new StringBuilder();
		String[] lines = sql.split("\n");
		int indentLevel = 0;

		for (String line : lines) {
			String trimmedLine = line.stripTrailing();

			if (trimmedLine.isEmpty()) {
				continue;
			}

			// 들여쓰기 감소 키워드 체크 (먼저 처리)
			if (startsWithKeywordIgnoreCase(trimmedLine, "END") || startsWithKeywordIgnoreCase(trimmedLine, ")")) {
				indentLevel = Math.max(0, indentLevel - 1);
			}

			// 들여쓰기 적용
			formatted.append(getIndent(indentLevel));

			// 키워드 대문자 변환 (옵션에 따라)
			if (uppercaseKeywords) {
				trimmedLine = uppercaseKeywordsInLine(trimmedLine);
			}

			formatted.append(trimmedLine).append("\n");

			// 들여쓰기 증가 키워드 체크
			if (startsWithKeywordIgnoreCase(trimmedLine, "SELECT") ||
				startsWithKeywordIgnoreCase(trimmedLine, "FROM") ||
				startsWithKeywordIgnoreCase(trimmedLine, "WHERE") ||
				startsWithKeywordIgnoreCase(trimmedLine, "CASE") ||
				startsWithKeywordIgnoreCase(trimmedLine, "(")) {
				indentLevel++;
			}
		}

		return formatted.toString();
	}

	/**
	 * 라인에서 SQL 키워드를 대문자로 변환
	 */
	private String uppercaseKeywordsInLine(String line) {
		for (String keyword : SQL_KEYWORDS) {
			Pattern pattern = Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE);
			line = pattern.matcher(line).replaceAll(keyword.toUpperCase());
		}
		return line;
	}

	/**
	 * 라인이 특정 키워드로 시작하는지 체크 (대소문자 무시)
	 */
	private boolean startsWithKeywordIgnoreCase(String line, String keyword) {
		return line.toUpperCase().startsWith(keyword.toUpperCase());
	}

	/**
	 * 들여쓰기 문자열 생성
	 */
	private String getIndent(int level) {
		return " ".repeat(Math.max(0, level * indentSize));
	}

	/**
	 * 값 포맷팅 (특수문자 이스케이프 포함)
	 */
	private String formatValue(Object value) {
		return switch (value) {
			case null -> "null";
			case String s -> {
				// 문자열의 작은따옴표를 이스케이프 처리
				String escapedValue = s.replace("'", "''");
				// 백슬래시 이스케이프 처리 (필요한 경우)
				escapedValue = escapedValue.replace("\\", "\\\\");
				yield "'" + escapedValue + "'";
			}
			case java.sql.Date date -> "'" + value + "'";
			case java.sql.Timestamp timestamp -> "'" + DATE_FORMAT.format(timestamp) + "'";
			case Date date -> "'" + DATE_FORMAT.format(date) + "'";
			case Boolean b -> b ? "1" : "0"; // 데이터베이스에 따라 조정 가능
			default -> value.toString();
		};
	}

	/**
	 * 쿼리 로그 출력
	 */
	private void printQueryLog(QueryExecutionInfo queryInfo) {
		StringBuilder log = new StringBuilder("\n");

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
			log.append("┃ Database Type   : ").append(dbInfo.getDatabaseType()).append("\n");
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
			// 느린 쿼리를 처리하기 위한 핸들러가 지정된 경우 해당 핸들러에게 위임
			if (getCmmnSlowQueryHandler() != null) {
				getCmmnSlowQueryHandler().handle(queryInfo);
			}
			log.append(" ⚠️ SLOW QUERY DETECTED!");
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

		if (includeStackTrace && e.getStackTrace().length > 0) {
			log.append("┃ Stack Trace     : \n");
			for (int i = 0; i < Math.min(5, e.getStackTrace().length); i++) {
				log.append("┃   at ").append(e.getStackTrace()[i]).append("\n");
			}
		}

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
			logger.error(log.toString());
			// logger.error(log.toString(), includeStackTrace ? e : null);
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

	/**
	 * 데이터베이스 타입 Enum
	 */
	public enum DatabaseType {
		ORACLE,
		MYSQL,
		MARIADB,
		POSTGRESQL,
		MSSQL,
		TIBERO,
		H2,
		HSQLDB,
		DERBY,
		UNKNOWN
	}

	// Inner Classes

	@Setter
	@Getter
	public static class QueryExecutionInfo {
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
		private String productName;
		private String productVersion;
		private String driverName;
		private String driverVersion;
		private String url;
		private String userName;
		private String schema;
		private String catalog;
		private DatabaseType databaseType;
	}

	@Setter
	@Getter
	public static class ParameterInfo {
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