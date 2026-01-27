package kr.co.peopleinsoft.cmmn.security.token.provider;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyPairGenerator;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumSigner;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import java.security.SecureRandom;
import java.security.Security;

public class CmmnDigitalSignProvider {

	DilithiumPrivateKeyParameters dilithiumPrivateKey;
	DilithiumPublicKeyParameters dilithiumPublicKey;

	static {
		/* Java Security API 를 통해 Dilithium 사용을 위한 Security Provider 등록 */
		if (Security.getProperty(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
			Security.addProvider(new BouncyCastlePQCProvider());
		}
	}

	public CmmnDigitalSignProvider() {
		DilithiumKeyGenerationParameters keyGenerationParameters = new DilithiumKeyGenerationParameters(new SecureRandom(), DilithiumParameters.dilithium3);
		DilithiumKeyPairGenerator keyPairGenerator = new DilithiumKeyPairGenerator();
		keyPairGenerator.init(keyGenerationParameters);

		AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();

		dilithiumPrivateKey = (DilithiumPrivateKeyParameters) keyPair.getPrivate();
		dilithiumPublicKey = (DilithiumPublicKeyParameters) keyPair.getPublic();
	}

	// 전자서명
	public byte[] sign(byte[] encryptedData) {
		// 암호화된 데이터에 전자서명 (privateKey 로 전자서명)
		DilithiumSigner dilithiumSigner = new DilithiumSigner();
		dilithiumSigner.init(true, dilithiumPrivateKey);
		return dilithiumSigner.generateSignature(encryptedData);
	}

	// 전자서명 검증
	public boolean verifySign(byte[] encryptedData, byte[] signedData) {
		DilithiumSigner dilithiumSigner = new DilithiumSigner();
		dilithiumSigner.init(false, dilithiumPublicKey);
		return dilithiumSigner.verifySignature(encryptedData, signedData);
	}
}