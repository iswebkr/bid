package kr.co.peopleinsoft.cmmn.mybatis.interceptor;

import kr.co.peopleinsoft.cmmn.util.utils.CmmnCaseUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Intercepts({
	@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}),
})
public class CmmnMyBatisCamelCaseInterceptor implements Interceptor {
	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		ResultSetHandler resultSetHandler = (ResultSetHandler) invocation.getTarget();
		MetaObject metaObject = SystemMetaObject.forObject(resultSetHandler);
		MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");

		List<ResultMap> resultMaps = mappedStatement.getResultMaps();
		for (ResultMap resultMap : resultMaps) {
			Class<?> resultMapType = resultMap.getType();
			if (HashMap.class.equals(resultMapType) || Map.class.equals(resultMapType)) {
				Object result = invocation.proceed();
				return CmmnCaseUtils.toCamelCase((List<Map<String, Object>>) result);
			}
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}
}