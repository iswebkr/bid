package kr.co.peopleinsoft.cmmn.util.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;

public class CmmnCSVFormatUtil {
	public static CSVFormat.Builder  defaultCsvBuilder() {
		return CSVFormat.EXCEL.builder()
			.setRecordSeparator(CmmnCSVConstants.CRLF)
			.setQuoteMode(QuoteMode.ALL)
			.setQuote(CmmnCSVConstants.DOUBLE_QUOTE_CHAR)
			.setDelimiter(CmmnCSVConstants.COMMA)
			.setEscape(CmmnCSVConstants.BACKSLASH)
			.setIgnoreEmptyLines(true)
			.setIgnoreSurroundingSpaces(true)
			.setTrim(true)
			.setNullString(CmmnCSVConstants.EMPTY);
	}

	public static  CSVFormat.Builder tabCsvBuilder() {
		return CSVFormat.EXCEL.builder()
			.setRecordSeparator(CmmnCSVConstants.CRLF)
			.setQuoteMode(QuoteMode.ALL)
			.setQuote(CmmnCSVConstants.DOUBLE_QUOTE_CHAR)
			.setDelimiter(CmmnCSVConstants.TAB)
			.setEscape(CmmnCSVConstants.BACKSLASH)
			.setIgnoreEmptyLines(true)
			.setIgnoreSurroundingSpaces(true)
			.setTrim(true)
			.setNullString(CmmnCSVConstants.EMPTY);
	}
}