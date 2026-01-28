package kr.co.peopleinsoft.cmmn.mybatis.handler.impl;

import kr.co.peopleinsoft.cmmn.mybatis.handler.CmmnSlowQueryHandler;
import kr.co.peopleinsoft.cmmn.mybatis.interceptor.CmmnMyBatisPrettyLogInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmmnSimpleQueryHandler implements CmmnSlowQueryHandler {

	private static final Logger logger = LoggerFactory.getLogger(CmmnSimpleQueryHandler.class);

	@Override
	public void handle(CmmnMyBatisPrettyLogInterceptor.QueryExecutionInfo queryInfo) {

	}
}