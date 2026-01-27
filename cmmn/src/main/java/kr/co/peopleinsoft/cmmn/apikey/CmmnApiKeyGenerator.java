package kr.co.peopleinsoft.cmmn.apikey;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 아직 개발 중
 * ApiKey 를 생성하고 hash 암호화 방식으로 저장 후 입력받은 provided key 값과
 * 비교하여 키가 맞는지 검토하는 과정 .. 을 거쳐 인증 진행
 * 실제로는 데이터베이스에 키 값을 저장하여 사용해야 함
 */
public class CmmnApiKeyGenerator {
	public String generateApiKey() {
		byte[] randomBytes = new byte[32];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(randomBytes);
		return Base64.getEncoder().withoutPadding().encodeToString(randomBytes);
	}

	public CmmnApiKeyDto secureApiKey() {
		String hashedKey = BCrypt.hashpw(generateApiKey(), BCrypt.gensalt());
		CmmnApiKeyDto apiKey = new CmmnApiKeyDto();
		apiKey.setApiKeyHash(hashedKey);
		return apiKey;
	}

	public boolean validateApiKey(String providedApiKey) {
		CmmnApiKeyDto apiKey = new CmmnApiKeyDto();
		return BCrypt.checkpw(providedApiKey, apiKey.getApiKeyHash());
	}
}