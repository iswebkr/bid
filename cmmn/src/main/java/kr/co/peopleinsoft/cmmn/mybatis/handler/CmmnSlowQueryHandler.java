package kr.co.peopleinsoft.cmmn.mybatis.handler;

import kr.co.peopleinsoft.cmmn.mybatis.interceptor.CmmnMyBatisPrettyLogInterceptor;

public interface CmmnSlowQueryHandler {
	void handle(CmmnMyBatisPrettyLogInterceptor.QueryExecutionInfo queryExecutionInfo);
}