package kr.co.peopleinsoft.cmmn.security.token.provider;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.AESDecrypter;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jwt.JWTClaimsSet;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class CmmnJweProvider {

	final SecretKey secretKey = Jwts.SIG.HS256.key().build();

	public byte[] generateToken(Authentication authentication, long expirationSeconds) throws JOSEException {
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
			.subject(authentication.getName())
			.claim("authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
			.issuer("application")
			.issueTime(Date.from(Instant.now()))
			.expirationTime(Date.from(Instant.now().plusSeconds(expirationSeconds)))
			.build();

		JWEHeader jweHeader = new JWEHeader.Builder(JWEAlgorithm.A256GCMKW, EncryptionMethod.A256GCM)
			.type(JOSEObjectType.JWT)
			.build();
		JWEObject jweObject = new JWEObject(jweHeader, new Payload(claimsSet.toJSONObject()));
		jweObject.encrypt(new AESEncrypter(secretKey));

		return jweObject.serialize().getBytes(StandardCharsets.UTF_8);
	}

	public boolean verifyToken(byte[] encryptedData) throws ParseException, JOSEException {
		String encryptedJweToken = new String(encryptedData, StandardCharsets.UTF_8);
		return verifyToken(encryptedJweToken);
	}

	public boolean verifyToken(String encryptedJweToken) throws ParseException, JOSEException {
		JWEObject jweObject = JWEObject.parse(encryptedJweToken);
		jweObject.decrypt(new AESDecrypter(secretKey));
		JWTClaimsSet jwtClaimsSet = JWTClaimsSet.parse(jweObject.getPayload().toJSONObject());
		Date expirationTime = jwtClaimsSet.getExpirationTime();
		
		// 현재 시간 기준으로 Token 유효시간이 아직 남아있으면 통과
		return expirationTime.after(new Date());
	}

	public Map<String, Object> decrypteToken(byte[] encryptedData) throws ParseException, JOSEException {
		String encryptedJweToken = new String(encryptedData, StandardCharsets.UTF_8);
		JWEObject jweObject = JWEObject.parse(encryptedJweToken);
		jweObject.decrypt(new AESDecrypter(secretKey));
		return jweObject.getPayload().toJSONObject();
	}
}