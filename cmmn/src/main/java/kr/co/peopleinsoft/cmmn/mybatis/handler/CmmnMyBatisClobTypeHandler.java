package kr.co.peopleinsoft.cmmn.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@MappedTypes(String.class)
@MappedJdbcTypes({JdbcType.CLOB, JdbcType.NCLOB})
public class CmmnMyBatisClobTypeHandler implements TypeHandler<String> {
	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		if(parameter==null) {
			ps.setNull(i, Types.CLOB);
		} else {
			ps.setClob(i, new StringReader(parameter), parameter.length());
		}
	}

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {
		Clob clob = rs.getClob(columnName);
		return clobToString(clob);
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {
		Clob clob =  rs.getClob(columnIndex);
		return clobToString(clob);
	}

	@Override
	public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
		Clob clob = cs.getClob(columnIndex);
		return clobToString(clob);
	}

	String clobToString(Clob clob) throws SQLException {
		try {
			return clob.getSubString(1, (int) clob.length());
		} catch (SQLException e) {
			return null;
		} finally {
			clob.free();
		}
	}
}