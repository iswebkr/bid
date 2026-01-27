package kr.co.peopleinsoft.cmmn.message;

import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CmmnReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

	static final Logger logger = LoggerFactory.getLogger(CmmnReloadableResourceBundleMessageSource.class);

	final NamedParameterJdbcTemplate jdbcTemplate;

	// 메시지 조회 쿼리
	final String messageQuery = """
			SELECT
					MESSAGE
			FROM    TB_MESSAGE
			WHERE   MESSAGE_CODE = :messageCode AND MESSAGE_LOCALE = :messageLocale
		""";

	public CmmnReloadableResourceBundleMessageSource(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		String message;

		if (logger.isInfoEnabled()) {
			logger.info("ResolveCodeWithoutArguments - Message : (code : {}, locale : {})", code, locale);
		}

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("messageCode", code);
		paramMap.put("messageLocale", locale.toString());

		try {
			message = super.resolveCodeWithoutArguments(code, locale);

			if(StringUtils.isNotBlank(message)) {
				message = jdbcTemplate.queryForObject(messageQuery, paramMap, String.class);
			}
			return message;
		} catch (DataAccessException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Message Resource Error : {}", e.getMessage());
			}
		}
		return null;
	}

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		String message;

		if (logger.isInfoEnabled()) {
			logger.info("ResolveCode - Message : (code : {}, locale : {})", code, locale);
		}

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("messageCode", code);
		paramMap.put("messageLocale", locale.toString());

		try {
			MessageFormat messageFormat = super.resolveCode(code, locale);

			if(messageFormat != null) {
				return messageFormat;
			} else {
				message = jdbcTemplate.queryForObject(messageQuery, paramMap, String.class);
				if (StringUtils.isNotBlank(message)) {
					return new MessageFormat(message);
				}
			}
		} catch (DataAccessException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Message Resource Error : {}", e.getMessage());
			}
		}

		return super.resolveCode(code, locale);
	}
}