package kr.co.peopleinsoft.cmmn.util.qrcode;

import com.google.zxing.WriterException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * QRCode 생성 예제)
 * ImageIO.write(CmmnQRGenerator.simpleQRCode("https://www.naver.com/"), "PNG", response.getOutputStream());
 * 		response.getOutputStream().flush();
 * 		response.getOutputStream().close();
 */
public class CmmnQRGenerator {
	public static BufferedImage simpleQRCode(String text) throws WriterException {
		return CmmnQRCodeBuilder.builder()
			.setText(text)
			.build();
	}

	public static BufferedImage wifiQRCode(String ssid, String password, String security, boolean hidden) throws WriterException {
		String wifiString = String.format("WIFI:T:%s;S:%s;P:%s;H:%s;;", security, ssid, password, hidden ? "true" : "false");
		return CmmnQRCodeBuilder.builder()
			.setText(wifiString)
			.build();
	}

	public static BufferedImage contactQRCode(String name, String phone, String email, String organization) throws WriterException {
		String vcardText = "BEGIN:VCARD\n" +
			"VERSION:3.0\n" +
			"FN:" + name + "\n" +
			"TEL:" + phone + "\n" +
			"EMAIL:" + email + "\n" +
			"ORG:" + organization + "\n" +
			"END:VCARD";

		return CmmnQRCodeBuilder.builder()
			.setText(vcardText)
			.setWidth(350)
			.setHeight(350)
			.build();
	}

	public static BufferedImage smsQRCode(String phoneNumber, String message) throws WriterException {
		String smsMessage = String.format("SMSTO:%s:%s", phoneNumber, message);
		return CmmnQRCodeBuilder.builder()
			.setText(smsMessage)
			.build();
	}

	public static BufferedImage emailQrCode(String email, String subject, String body) throws WriterException {
		String emailBody = String.format("mailto:%s?subject=%s&body=%s", email, subject, body);
		return CmmnQRCodeBuilder.builder()
			.setText(emailBody)
			.build();
	}

	public static BufferedImage locationQRCode(double latitude, double longitude) throws WriterException {
		String locationString = String.format("geo:%f,%f", latitude, longitude);
		return CmmnQRCodeBuilder.builder()
			.setText(locationString)
			.build();
	}

	public static byte[] toByteArray(BufferedImage image, String format) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(image, format, byteArrayOutputStream);
		return byteArrayOutputStream.toByteArray();
	}

	public static BufferedImage addLogoToQRCode(BufferedImage qrImage, BufferedImage logoImage, int logoSize) throws WriterException {
		int qrWidth = qrImage.getWidth();
		int qrHeight = qrImage.getHeight();

		//로고 사이즈 조정
		BufferedImage scaledLogoImage = new BufferedImage(logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D logoGraphics = scaledLogoImage.createGraphics();
		logoGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		logoGraphics.drawImage(logoImage, 0, 0, logoSize, logoSize, null);
		logoGraphics.dispose();

		// QR코드에 로고 합성
		BufferedImage compositeImage = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D mergeGraphic = compositeImage.createGraphics();
		mergeGraphic.drawImage(qrImage, 0, 0, null);

		// 로그를 중앙에 위치
		int logoX = (qrWidth - logoSize) / 2;
		int logoY = (qrHeight - logoSize) / 2;
		mergeGraphic.drawImage(scaledLogoImage, logoX, logoY, null);
		mergeGraphic.dispose();

		return compositeImage;
	}
}