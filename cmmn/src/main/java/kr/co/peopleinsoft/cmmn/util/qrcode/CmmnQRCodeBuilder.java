package kr.co.peopleinsoft.cmmn.util.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CmmnQRCodeBuilder {
	String text;
	int width = 300;
	int height = 300;
	ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.M;
	String charset = StandardCharsets.UTF_8.name();
	int margin = 0;
	Color foregroundColor = Color.BLACK;
	Color backgroundColor = Color.WHITE;

	public static CmmnQRCodeBuilder builder() {
		return new CmmnQRCodeBuilder();
	}

	public CmmnQRCodeBuilder setText(String text) {
		this.text = text;
		return this;
	}

	public CmmnQRCodeBuilder setWidth(int width) {
		this.width = width;
		return this;
	}

	public CmmnQRCodeBuilder setHeight(int height) {
		this.height = height;
		return this;
	}

	public CmmnQRCodeBuilder setErrorCorrectionLevel(ErrorCorrectionLevel errorCorrectionLevel) {
		this.errorCorrectionLevel = errorCorrectionLevel;
		return this;
	}

	public CmmnQRCodeBuilder setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public CmmnQRCodeBuilder setMargin(int margin) {
		this.margin = margin;
		return this;
	}

	public CmmnQRCodeBuilder setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
		return this;
	}

	public CmmnQRCodeBuilder setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public BufferedImage build() throws WriterException {
		if (text.isEmpty()) {
			throw new IllegalArgumentException("text is empty");
		}

		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
		hints.put(EncodeHintType.CHARACTER_SET, charset);
		hints.put(EncodeHintType.MARGIN, margin);

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

		MatrixToImageConfig imageConfig = new MatrixToImageConfig(foregroundColor.getRGB(), backgroundColor.getRGB());

		return MatrixToImageWriter.toBufferedImage(bitMatrix, imageConfig);
	}
}