package kr.co.peopleinsoft.cmmn.dilithium;

import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

public class CmmnDilithiumSinger {
	public static KeyPair generateDilithiumKeyPair() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("Dilithium", "BCPQC");
		keyPairGenerator.initialize(DilithiumParameterSpec.dilithium3, new SecureRandom());
		return keyPairGenerator.generateKeyPair();
	}

	public static byte[] signData(PrivateKey privateKey, byte[] data) throws Exception {
		Signature signature = Signature.getInstance("Dilithium", "BCPQC");
		signature.initSign(privateKey);
		signature.update(data);
		return signature.sign();
	}

	public static boolean verifySignature(PublicKey publicKey, byte[] data, byte[] signatureBytes) throws Exception {
		Signature signature = Signature.getInstance("Dilithium", "BCPQC");
		signature.initVerify(publicKey);
		signature.update(data);
		return signature.verify(signatureBytes);
	}
}