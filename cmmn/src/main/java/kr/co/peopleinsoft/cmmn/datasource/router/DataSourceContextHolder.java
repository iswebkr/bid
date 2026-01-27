package kr.co.peopleinsoft.cmmn.datasource.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceContextHolder {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceContextHolder.class);
	private static final InheritableThreadLocal<DataSourceType> contextHolder = new InheritableThreadLocal<>();

	public static void setDataSourceType(DataSourceType dataSourceType) {
		if (dataSourceType == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("데이터소스 null 전달: Thread [{}] -> DEFAULT 사용", Thread.currentThread().getName());
			}
			dataSourceType = DataSourceType.DEFAULT;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("데이터소스 설정: Thread [{}] -> DataSource [{}]", Thread.currentThread().getName(), dataSourceType);
		}

		contextHolder.set(dataSourceType);
	}

	public static DataSourceType getDataSourceType() {
		DataSourceType dataSourceType = contextHolder.get();

		if (dataSourceType == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("데이터소스 미설정: Thread [{}] -> DEFAULT 반환", Thread.currentThread().getName());
			}
			return DataSourceType.DEFAULT;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("현재 Thread [{}] 의 데이터소스는 [{}] 입니다.", Thread.currentThread().getName(), dataSourceType);
		}

		return dataSourceType;
	}

	public static void clearDataSourceType() {
		DataSourceType dataSourceType = contextHolder.get();

		if (logger.isDebugEnabled()) {
			logger.debug("데이터소스 해제: Thread [{}] -> DataSource [{}]", Thread.currentThread().getName(), dataSourceType != null ? dataSourceType : "null");
		}

		contextHolder.remove();
	}

	public static boolean hasDataSourceType() {
		return contextHolder.get() != null;
	}
}