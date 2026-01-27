package kr.co.peopleinsoft.cmmn.util.pdf;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CmmnPdfUtil {
	public static void createPdf(String pdfFileName, String uri) throws IOException {
		try (OutputStream fileOutputStream = new FileOutputStream(pdfFileName)) {
			new PdfRendererBuilder().useFastMode()
				.useFastMode()
				.useFont(new File("src/main/resources/fonts/NotoSansKR-Regular.ttf"), "NotoSansKR", 400, BaseRendererBuilder.FontStyle.NORMAL, true)
				.useDefaultPageSize(210, 297, BaseRendererBuilder.PageSizeUnits.MM)
				.withUri(uri)
				.toStream(fileOutputStream)
				.run();
		}
	}
}