package kr.co.peopleinsoft.cmmn.security.token;

import com.nimbusds.jose.JOSEException;
import kr.co.peopleinsoft.cmmn.security.token.provider.CmmnDigitalSignProvider;
import kr.co.peopleinsoft.cmmn.security.token.provider.CmmnJweProvider;
import org.springframework.security.core.Authentication;

import java.text.ParseException;
import java.util.Base64;
import java.util.Map;

public class CmmnSecureTokenProvider {

	final CmmnJweProvider jweProvider;
	final CmmnDigitalSignProvider digitalSignProvider;

	public CmmnSecureTokenProvider(CmmnJweProvider jweProvider, CmmnDigitalSignProvider digitalSignProvider) {
		this.jweProvider = jweProvider;
		this.digitalSignProvider = digitalSignProvider;
	}

	public String generateSecureToken(Authentication authentication, long expirationSeconds) throws JOSEException {
		byte[] encryptedData = jweProvider.generateToken(authentication, expirationSeconds);
		byte[] signedData = digitalSignProvider.sign(encryptedData);
		SecureToken secureToken = new SecureToken(encryptedData, signedData);
		return secureToken.base64String();
	}

	public boolean verifySecureToken(String base64String) throws ParseException, JOSEException {
		SecureToken secureToken = SecureToken.fromBase64String(base64String);
		return jweProvider.verifyToken(secureToken.encryptedData()) && digitalSignProvider.verifySign(secureToken.encryptedData(), secureToken.signature());
	}

	public Map<String, Object> decryptToken(String base64String) throws ParseException, JOSEException {
		SecureToken secureToken = SecureToken.fromBase64String(base64String);
		return jweProvider.decrypteToken(secureToken.encryptedData());
	}

	public record SecureToken(byte[] encryptedData,
	                          byte[] signature) {
		public String base64String() {
			return Base64.getEncoder().encodeToString(encryptedData) +
				"." +
				Base64.getEncoder().encodeToString(signature);
		}

		public static SecureToken fromBase64String(String base64String) {
			String[] tokens = base64String.split("\\.");
			byte[] encryptedData = Base64.getDecoder().decode(tokens[0]);
			byte[] signedData = Base64.getDecoder().decode(tokens[1]);
			return new SecureToken(encryptedData, signedData);
		}
	}
}