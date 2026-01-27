package kr.co.peopleinsoft.cmmn.util.utils;

import org.apache.commons.text.CaseUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CamelCase Utility
 */
public class CmmnCaseUtils {
	/**
	 * Map 이 담겨진 List 의 key 값을 CamelCase 형태로 변경
	 */
	public static List<Map<String, Object>> toCamelCase(List<Map<String, Object>> list) {
		if (list == null || list.isEmpty()) {
			return list;
		}
		return list.stream().map(CmmnCaseUtils::toCamelCase).toList();
	}

	/**
	 * Map 에 담긴 Key 값을 CamelCase 로 변경
	 */
	public static Map<String, Object> toCamelCase(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return map;
		}
		HashMap<String, Object> resultMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			resultMap.put(toCamelCase(key, '_', ' ', '-'), entry.getValue());
		}
		return resultMap;
	}

	/**
	 * 첫번째 파라미터의 문자열을 두번째 파라미터의 구분자를 기준으로 CamelCase 로 변경
	 */
	public static String toCamelCase(String str, char... delimiters) {
		return CaseUtils.toCamelCase(str, false, delimiters);
	}
}