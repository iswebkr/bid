package kr.co.peopleinsoft.cmmn.egovframe;

import org.egovframe.rte.fdl.cmmn.aspect.ExceptionTransfer;
import org.egovframe.rte.fdl.cmmn.exception.handler.ExceptionHandler;
import org.egovframe.rte.fdl.cmmn.exception.manager.DefaultExceptionHandleManager;
import org.egovframe.rte.fdl.cmmn.exception.manager.ExceptionHandlerService;
import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.egovframe.rte.fdl.cmmn.trace.handler.TraceHandler;
import org.egovframe.rte.fdl.cmmn.trace.manager.DefaultTraceHandleManager;
import org.egovframe.rte.fdl.cmmn.trace.manager.TraceHandlerService;
import org.egovframe.rte.fdl.cryptography.EgovARIACryptoService;
import org.egovframe.rte.fdl.cryptography.EgovDigestService;
import org.egovframe.rte.fdl.cryptography.EgovGeneralCryptoService;
import org.egovframe.rte.fdl.cryptography.EgovPasswordEncoder;
import org.egovframe.rte.fdl.cryptography.impl.EgovARIACryptoServiceImpl;
import org.egovframe.rte.fdl.cryptography.impl.EgovDigestServiceImpl;
import org.egovframe.rte.fdl.cryptography.impl.EgovEnvCryptoServiceImpl;
import org.egovframe.rte.fdl.cryptography.impl.EgovGeneralCryptoServiceImpl;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.egovframe.rte.fdl.idgnr.impl.EgovTableIdGnrServiceImpl;
import org.egovframe.rte.fdl.idgnr.impl.strategy.EgovIdGnrStrategyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.AntPathMatcher;

import javax.sql.DataSource;

@Configuration
public class CmmnEgovFrameConfig {

	static final Logger logger = LoggerFactory.getLogger(CmmnEgovFrameConfig.class);
	final ConfigurableEnvironment environment;

	public CmmnEgovFrameConfig(ConfigurableEnvironment environment) {
		this.environment = environment;
	}

	@Bean
	EgovPasswordEncoder egovPasswordEncoder() {
		EgovPasswordEncoder egovPasswordEncoder = new EgovPasswordEncoder();
		egovPasswordEncoder.setAlgorithm(environment.getProperty("crypto.algorithm"));
		egovPasswordEncoder.setHashedPassword(environment.getProperty("crypto.hashed.password"));
		return egovPasswordEncoder;
	}

	@Bean
	EgovARIACryptoService egovARIACryptoService(EgovPasswordEncoder egovPasswordEncoder) {
		EgovARIACryptoServiceImpl egovARIACryptoService = new EgovARIACryptoServiceImpl();
		egovARIACryptoService.setPasswordEncoder(egovPasswordEncoder);
		egovARIACryptoService.setBlockSize(2048);
		return egovARIACryptoService;
	}

	@Bean
	EgovDigestService egovDigestService() {
		EgovDigestServiceImpl egovDigestService = new EgovDigestServiceImpl();
		egovDigestService.setAlgorithm(environment.getProperty("crypto.algorithm"));
		egovDigestService.setPlainDigest(false);
		return egovDigestService;
	}

	@Bean
	EgovGeneralCryptoService egovGeneralCryptoService(EgovPasswordEncoder egovPasswordEncoder) {
		EgovGeneralCryptoService egovGeneralCryptoService = new EgovGeneralCryptoServiceImpl();
		egovGeneralCryptoService.setPasswordEncoder(egovPasswordEncoder());
		egovGeneralCryptoService.setAlgorithm(environment.getProperty("crypto.cryptoServiceAlgorithm"));
		egovGeneralCryptoService.setBlockSize(2048);
		return egovGeneralCryptoService;
	}

	@Bean
	EgovEnvCryptoServiceImpl egovEnvCryptoService(EgovPasswordEncoder egovPasswordEncoder, EgovGeneralCryptoService egovGeneralCryptoService) {
		EgovEnvCryptoServiceImpl egovEnvCryptoService = new EgovEnvCryptoServiceImpl();
		egovEnvCryptoService.setPasswordEncoder(egovPasswordEncoder);
		egovEnvCryptoService.setCryptoService(egovGeneralCryptoService);
		egovEnvCryptoService.setCryptoAlgorithm(environment.getProperty("crypto.algorithm"));
		egovEnvCryptoService.setCyptoAlgorithmKey(environment.getProperty("crypto.algorithmKey"));
		egovEnvCryptoService.setCyptoAlgorithmKeyHash(environment.getProperty("crypto.algorithmKeyHash"));
		egovEnvCryptoService.setCryptoBlockSize(2048);
		return egovEnvCryptoService;
	}

	@Bean
	LeaveaTrace leaveaTrace() {
		LeaveaTrace leaveaTrace = new LeaveaTrace();
		CmmnTraceHandlerManager cmmnTraceHandlerManager = new CmmnTraceHandlerManager();
		cmmnTraceHandlerManager.setReqExpMatcher(new AntPathMatcher());
		cmmnTraceHandlerManager.setPatterns(new String[]{"*"});
		cmmnTraceHandlerManager.setHandlers(new TraceHandler[]{
			(clazz, message) -> {
				if (logger.isDebugEnabled()) {
					logger.debug("Exception 을 발생시키지 않고 수행하고자 하는 처리로직 추가");
				}
			}
		});

		leaveaTrace.setTraceHandlerServices(new TraceHandlerService[]{
			cmmnTraceHandlerManager
		});
		return leaveaTrace;
	}

	/**
	 * TODO : 서비스 Exception Transfer 설정은 여기에 해야함
	 */
	@Bean
	ExceptionTransfer exceptionTransfer() {
		ExceptionTransfer exceptionTransfer = new ExceptionTransfer();
		CmmnExceptionHandlerManager cmmnExceptionHandlerManager = new CmmnExceptionHandlerManager();
		cmmnExceptionHandlerManager.setPatterns(new String[]{
			"kr.co.peopleinsoft.war.service.*"
		});
		cmmnExceptionHandlerManager.setHandlers(new ExceptionHandler[]{
			(exception, pkg) -> {
				if (logger.isDebugEnabled()) {
					logger.info("CustomerHandler... 실행");
				}
			}
		});
		exceptionTransfer.setExceptionHandlerService(new ExceptionHandlerService[]{
			cmmnExceptionHandlerManager
		});
		return exceptionTransfer;
	}

	@Bean
	EgovIdGnrService egovUUIdGenService(DataSource dataSource, EgovIdGnrStrategyImpl egovIdGnrStrategy) {
		EgovTableIdGnrServiceImpl egovTableIdGnrService = new EgovTableIdGnrServiceImpl();
		egovTableIdGnrService.setDataSource(dataSource);
		egovTableIdGnrService.setStrategy(egovIdGnrStrategy);
		egovTableIdGnrService.setBlockSize(10);
		egovTableIdGnrService.setTable("IDS");
		egovTableIdGnrService.setTableName("SAMPLE");
		egovTableIdGnrService.setQuery("SELECT TEST.NEXTVAL FROM DUAL");
		return egovTableIdGnrService;
	}

	@Bean
	EgovIdGnrStrategyImpl egovIdGnrStrategy() {
		EgovIdGnrStrategyImpl egovIdGnrStrategy = new EgovIdGnrStrategyImpl();
		egovIdGnrStrategy.setPrefix("SAMPLE-");
		egovIdGnrStrategy.setCipers(5);
		egovIdGnrStrategy.setFillChar((char) 0);
		return egovIdGnrStrategy;
	}

	static class CmmnTraceHandlerManager extends DefaultTraceHandleManager {
	}

	static class CmmnExceptionHandlerManager extends DefaultExceptionHandleManager {
	}
}