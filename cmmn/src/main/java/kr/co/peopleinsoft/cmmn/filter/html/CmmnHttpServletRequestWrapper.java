package kr.co.peopleinsoft.cmmn.filter.html;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;

public class CmmnHttpServletRequestWrapper extends HttpServletRequestWrapper {
	public CmmnHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		if (values != null) {
			for (int i = 0; i < values.length; ++i) {
				if (values[i] != null) {
					values[i] = this.getSafeParamData(values[i]);
				} else {
					values[i] = null;
				}
			}
			return values;
		}
		return null;
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if (value != null) {
			value = this.getSafeParamData(value);
			return value;
		}
		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> valueMap = super.getParameterMap();
		for (String key : valueMap.keySet()) {
			String[] values = valueMap.get(key);
			for (int i = 0; i < values.length; ++i) {
				if (values[i] != null) {
					values[i] = this.getSafeParamData(values[i]);
				} else {
					values[i] = null;
				}
			}
		}
		return valueMap;
	}

	String getSafeParamData(String value) {
		return HtmlUtils.htmlEscape(value);
	}
}