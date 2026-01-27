package kr.co.peopleinsoft.cmmn.egovframe.crypto;

import org.egovframe.rte.fdl.cryptography.EgovPasswordEncoder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

public class EgovEncCryptoAlgorithmCreateTest {
	static void main(String[] args) throws IOException {
		Resource resource = new ClassPathResource("application.properties");
		Properties properties = new Properties();
		properties.load(resource.getInputStream());

		String algorithm = properties.getProperty("crypto.algorithm");
		String algorithmKey = properties.getProperty("crypto.algorithmKey");

		EgovPasswordEncoder passwordEncoder = new EgovPasswordEncoder();
		passwordEncoder.setAlgorithm(properties.getProperty("crypto.algorithm"));

		System.out.println();

		System.out.println("================================================");
		System.out.println("알고리즘 : " + algorithm);
		System.out.println("알고리즘 키 : " + algorithmKey);
		System.out.println("알고리즘 키 Hash : "  + passwordEncoder.encryptPassword(algorithmKey));
		System.out.println("알고리즘 블럭사이즈 : " + 2048);
	}
}