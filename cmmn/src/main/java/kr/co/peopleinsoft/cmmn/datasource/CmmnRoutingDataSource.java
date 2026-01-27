package kr.co.peopleinsoft.cmmn.datasource;

import kr.co.peopleinsoft.cmmn.datasource.router.DataSourceContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

public class CmmnRoutingDataSource extends AbstractRoutingDataSource {

	static final Logger logger = LoggerFactory.getLogger(CmmnRoutingDataSource.class);

	@Override
	protected Object determineCurrentLookupKey() {
		String dataSourceType =  DataSourceContextHolder.getDataSourceType().toString();
		logger.debug("Current DataSource is " + dataSourceType);
		return DataSourceContextHolder.getDataSourceType();
	}

	@Override
	protected DataSource determineTargetDataSource() {
		Object lookupKey = determineCurrentLookupKey();
		DataSource dataSource = super.determineTargetDataSource();
		logger.debug("Determined target DataSource for Key [{}]  : {}", lookupKey, dataSource.getClass().getSimpleName());
		return dataSource;
	}
}