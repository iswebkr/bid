package kr.co.peopleinsoft.cmmn.mybatis.util;

import org.apache.commons.lang3.StringUtils;

public class MyBatisUtil {
	public boolean isNotEmpty(String str) {
		return StringUtils.isNotBlank(str);
	}

	public boolean isEmpty(String str) {
		return StringUtils.isBlank(str);
	}
}