package kr.co.peopleinsoft.cmmn.egovframe.crypto;

import org.egovframe.rte.fdl.cryptography.EgovGeneralCryptoService;
import org.egovframe.rte.fdl.cryptography.EgovPasswordEncoder;
import org.egovframe.rte.fdl.cryptography.impl.EgovEnvCryptoServiceImpl;
import org.egovframe.rte.fdl.cryptography.impl.EgovGeneralCryptoServiceImpl;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

public class EgovEncCryptoAlgorithmUserTest {
	static void main(String[] args) throws IOException {
		Resource resource = new ClassPathResource("application.properties");
		Properties properties = new Properties();
		properties.load(resource.getInputStream());

		Resource targetResource = new ClassPathResource("database-dev.properties");
		Properties targetProperties = new Properties();
		targetProperties.load(targetResource.getInputStream());

		EgovPasswordEncoder egovPasswordEncoder = new EgovPasswordEncoder();
		egovPasswordEncoder.setAlgorithm(properties.getProperty("crypto.algorithm"));
		egovPasswordEncoder.setHashedPassword(properties.getProperty("crypto.algorithmKeyHash"));

		EgovGeneralCryptoService egovGeneralCryptoService = new EgovGeneralCryptoServiceImpl();
		egovGeneralCryptoService.setPasswordEncoder(egovPasswordEncoder);
		egovGeneralCryptoService.setAlgorithm(properties.getProperty("crypto.cryptoServiceAlgorithm"));
		egovGeneralCryptoService.setBlockSize(Integer.parseInt(properties.getProperty("crypto.algorithmKeyHash")));

		EgovEnvCryptoServiceImpl egovEnvCryptoService = new EgovEnvCryptoServiceImpl();
		egovEnvCryptoService.setPasswordEncoder(egovPasswordEncoder);
		egovEnvCryptoService.setCryptoService(egovGeneralCryptoService);
		egovEnvCryptoService.setCryptoAlgorithm(properties.getProperty("crypto.algorithm"));
		egovEnvCryptoService.setCyptoAlgorithmKey(properties.getProperty("crypto.algorithmKey"));
		egovEnvCryptoService.setCyptoAlgorithmKeyHash(properties.getProperty("crypto.algorithmKeyHash"));
		egovEnvCryptoService.setCryptoBlockSize(Integer.parseInt(properties.getProperty("crypto.algorithmKeyHash")));

		targetProperties.forEach((key, value) -> System.out.println(key + "=" + egovEnvCryptoService.encrypt(String.valueOf(value))));
	}
}