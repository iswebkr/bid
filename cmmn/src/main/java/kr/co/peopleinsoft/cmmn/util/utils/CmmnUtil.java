package kr.co.peopleinsoft.cmmn.util.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CmmnUtil {
	static final Logger logger = LoggerFactory.getLogger(CmmnUtil.class);

	public static Properties loadProperties(String propertiesFileName) {
		Properties properties = new Properties();
		ClassLoader loader = CmmnUtil.class.getClassLoader();
		try (InputStream inputStream = loader.getResourceAsStream(propertiesFileName)) {
			if (inputStream != null) {
				properties.load(inputStream);
			}
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Failed to load circuitbreaker.properties: {}", e.getMessage());
			}
		}
		return properties;
	}
}