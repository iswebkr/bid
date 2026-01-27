package kr.co.peopleinsoft.cmmn.util.poi;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

public class CmmnPoiUtil {
	public static void createExcel(String excelFile, String sheetName, List<String> headers, List<List<Object>> dataRows) throws IOException {
		String fileExtension = FilenameUtils.getExtension(excelFile);

		if (".xlsx".equals(fileExtension)) {
			xlsx(excelFile, sheetName, headers, dataRows);
		} else {
			xls(excelFile, sheetName, headers, dataRows);
		}
	}

	private static void xls(String excelFile, String sheetName, List<String> headers, List<List<Object>> dataRows) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(excelFile)) {
			extracted(sheetName, headers, dataRows, workbook);
			workbook.write(fileOut);
		}
	}

	private static void xlsx(String excelFile, String sheetName, List<String> headers, List<List<Object>> dataRows) throws IOException {
		try (Workbook workbook = new SXSSFWorkbook(1000); FileOutputStream fileOut = new FileOutputStream(excelFile)) {
			extracted(sheetName, headers, dataRows, workbook);
			workbook.write(fileOut);
		}
	}

	static void extracted(String sheetName, List<String> headers, List<List<Object>> dataRows, Workbook workbook) {
		Sheet sheet = workbook.createSheet(sheetName);

		Row headerRow = sheet.createRow(0);
		IntStream.range(0, headers.size()).forEach(index -> {
			headerRow.createCell(index).setCellValue(headers.get(index));
		});

		IntStream.range(0, dataRows.size()).forEach(index -> {
			int rowIdx = index + 1;
			Row dataRow = sheet.createRow(rowIdx);
			List<Object> items = dataRows.get(index);
			IntStream.range(0, items.size()).forEach(itemIndex -> {
				Object value = items.get(itemIndex);
				Cell cell = dataRow.createCell(itemIndex);
				if (value != null) {
					switch (value) {
						case Number number -> cell.setCellValue(number.doubleValue());
						case Boolean b -> cell.setCellValue(b);
						case LocalDateTime localDateTime -> cell.setCellValue(localDateTime);
						default -> cell.setCellValue(value.toString());
					}
				}
			});
		});
	}
}