package kr.co.peopleinsoft.biz.mapper;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;
import java.util.Map;

public class CmmnMapper {
	private final SqlSessionTemplate sqlSessionTemplate;
	private final SqlSessionTemplate batchSqlSessionTemplate;
	private final SqlSessionTemplate reuseSqlSessionTemplate;

	public CmmnMapper(SqlSessionTemplate sqlSessionTemplate, SqlSessionTemplate batchSqlSessionTemplate, SqlSessionTemplate reuseSqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
		this.batchSqlSessionTemplate = batchSqlSessionTemplate;
		this.reuseSqlSessionTemplate = reuseSqlSessionTemplate;
	}

	public void select(String statement, ResultHandler<?> handler) {
		getSqlSessionTemplate(statement).select(statement, handler);
	}

	public void select(String statement, Object parameter, ResultHandler<?> handler) {
		getSqlSessionTemplate(statement).select(statement, parameter, handler);
	}

	public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler<?> handler) {
		getSqlSessionTemplate(statement).select(statement, parameter, rowBounds, handler);
	}

	public <T> T selectOne(String statement) {
		return getSqlSessionTemplate(statement).selectOne(statement);
	}

	public <T> T selectOne(String statement, Object parameter) {
		return getSqlSessionTemplate(statement).selectOne(statement, parameter);
	}

	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		return getSqlSessionTemplate(statement).selectMap(statement, mapKey);
	}

	public <K, V> Map<K, V> selectMap(String statement, Object object, String mapKey) {
		return getSqlSessionTemplate(statement).selectMap(statement, object, mapKey);
	}

	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
		return getSqlSessionTemplate(statement).selectMap(statement, parameter, mapKey, rowBounds);
	}

	public <T> Cursor<T> selectCursor(String statement) {
		return getSqlSessionTemplate(statement).selectCursor(statement);
	}

	public <T> Cursor<T> selectCursor(String statement, Object parameter) {
		return getSqlSessionTemplate(statement).selectCursor(statement, parameter);
	}

	public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
		return getSqlSessionTemplate(statement).selectCursor(statement, parameter, rowBounds);
	}

	public <T> List<T> selectList(String statement) {
		return getSqlSessionTemplate(statement).selectList(statement);
	}

	public <T> List<T> selectList(String statement, Object parameter) {
		return getSqlSessionTemplate(statement).selectList(statement, parameter);
	}

	public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
		return getSqlSessionTemplate(statement).selectList(statement, parameter, rowBounds);
	}

	public <T> List<T> selectPaginatedList(String statement, Object parameter, int offset, int limit) {
		return getSqlSessionTemplate(statement).selectList(statement, parameter, new RowBounds(offset, limit));
	}

	public <K, V> Map<K, V> selectPaginatedMap(String statement, Object object, String mapKey, int offset, int limit) {
		return getSqlSessionTemplate(statement).selectMap(statement, object, mapKey, new RowBounds(offset, limit));
	}

	public int insert(String statement) {
		return getSqlSessionTemplate(statement).insert(statement);
	}

	public int insert(String statement, Object parameter) {
		return getSqlSessionTemplate(statement).insert(statement, parameter);
	}

	public int update(String statement) {
		return getSqlSessionTemplate(statement).update(statement);
	}

	public int update(String statement, Object parameter) {
		return getSqlSessionTemplate(statement).update(statement, parameter);
	}

	public int delete(String statement) {
		return getSqlSessionTemplate(statement).delete(statement);
	}

	public int delete(String statement, Object parameter) {
		return getSqlSessionTemplate(statement).delete(statement, parameter);
	}

	/**
	 * Argument 로 넘어온 statement 에 "batch" 또는 "reuse" 가 존재하는 경우에 따라 각각의 SqlSessiontemplate 를 사용
	 * 만약 batch / reuse 가 포함되지 않은 경우라면 simple (기본) 을 사용
	 */
	SqlSessionTemplate getSqlSessionTemplate(String statement) {
		if (statement.toLowerCase().contains("batch")) {
			return batchSqlSessionTemplate;
		} else if (statement.toLowerCase().contains("reuse")) {
			return reuseSqlSessionTemplate;
		} else {
			return sqlSessionTemplate;
		}
	}
}