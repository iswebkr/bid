package kr.co.peopleinsoft.cmmn.datasource;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CmmnDataSourceTransactionConfig {
	/*** 트랜잭션 매니저 설정 */
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	PlatformTransactionManager transactionManager(DataSource dataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
		transactionManager.setDefaultTimeout(60 * 10); // 10분
		transactionManager.setRollbackOnCommitFailure(true);        // 커밋 실패시 롤백
		transactionManager.setValidateExistingTransaction(true);    // 기존 트랜잭션 검증
		transactionManager.setGlobalRollbackOnParticipationFailure(true);   // 참여 실패 시 글로벌 롤백
		return transactionManager;
	}

	/*** 트랜잭션 인터셉터 설정 ***/
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	TransactionInterceptor txAdvice(PlatformTransactionManager transactionManager) {
		NameMatchTransactionAttributeSource transactionAttributeSource = new NameMatchTransactionAttributeSource();
		Map<String, TransactionAttribute> txMethod = new HashMap<>();

		// 읽기 전용 트랜잭션 속성
		RuleBasedTransactionAttribute readonlyTransactionAttribute = new RuleBasedTransactionAttribute();
		readonlyTransactionAttribute.setName("READ_ONLY_TRANSACTION");
		readonlyTransactionAttribute.setReadOnly(true);
		readonlyTransactionAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
		readonlyTransactionAttribute.setTimeout(3600);

		// 읽기 전용 트랜잭션 적용
		txMethod.put("select*", readonlyTransactionAttribute);
		txMethod.put("get*", readonlyTransactionAttribute);
		txMethod.put("search*", readonlyTransactionAttribute);
		txMethod.put("retrieve*", readonlyTransactionAttribute);
		txMethod.put("find*", readonlyTransactionAttribute);
		txMethod.put("read*", readonlyTransactionAttribute);
		txMethod.put("count*", readonlyTransactionAttribute);
		txMethod.put("exists*", readonlyTransactionAttribute);
		txMethod.put("list*", readonlyTransactionAttribute);
		txMethod.put("is*", readonlyTransactionAttribute);
		txMethod.put("has*", readonlyTransactionAttribute);

		// 읽기/쓰기 트랜잭션 속성
		RuleBasedTransactionAttribute readWriteTransactionAttribute = new RuleBasedTransactionAttribute();
		readWriteTransactionAttribute.setName("READ_WRITE_TRANSACTION");
		readWriteTransactionAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		readWriteTransactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT); // DBMS 의 기본 격리 수준 유지
		readWriteTransactionAttribute.setTimeout(3600);
		readWriteTransactionAttribute.setRollbackRules(List.of(new RollbackRuleAttribute(Exception.class)));

		// 읽기/쓰기 트랜잭션 적용
		txMethod.put("insert*", readWriteTransactionAttribute);
		txMethod.put("save*", readWriteTransactionAttribute);
		txMethod.put("store*", readWriteTransactionAttribute);
		txMethod.put("create*", readWriteTransactionAttribute);
		txMethod.put("update*", readWriteTransactionAttribute);
		txMethod.put("modify*", readWriteTransactionAttribute);
		txMethod.put("modifi*", readWriteTransactionAttribute);
		txMethod.put("delete*", readWriteTransactionAttribute);
		txMethod.put("remove*", readWriteTransactionAttribute);
		txMethod.put("process*", readWriteTransactionAttribute);
		txMethod.put("execute*", readWriteTransactionAttribute);
		txMethod.put("merge*", readWriteTransactionAttribute);

		// 배치 처리용 트랜잭션 속성
		RuleBasedTransactionAttribute batchTransactionAttribute = new RuleBasedTransactionAttribute();
		batchTransactionAttribute.setName("BATCH_TRANSACTION");
		batchTransactionAttribute.setReadOnly(false);
		batchTransactionAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		batchTransactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED); // 커밋된 데이터만 읽을 수 있음
		batchTransactionAttribute.setTimeout(3600);
		batchTransactionAttribute.setRollbackRules(List.of(new RollbackRuleAttribute(Exception.class)));

		// 배치 처리용 트랜잭션 적용
		txMethod.put("batch*", batchTransactionAttribute);

		// 기본 트랜잭션 적용
		txMethod.put("*", readWriteTransactionAttribute);

		transactionAttributeSource.setNameMap(txMethod);

		TransactionInterceptor interceptor = new TransactionInterceptor();
		interceptor.setTransactionManager(transactionManager);
		interceptor.setTransactionAttributeSource(transactionAttributeSource);

		return interceptor;
	}

	/*** 트랜잭션 어드바이저 (@Transactional 미적용 메서드에만 적용) ***/
	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	Advisor txAdviceAdvisor(TransactionInterceptor txAdvice) {
		AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
		aspectJExpressionPointcut.setExpression("@within(org.springframework.stereotype.Service)  " +
			"&& execution(public * *(..)) " +
			"&& !@annotation(org.springframework.transaction.annotation.Transactional)");

		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
		advisor.setPointcut(aspectJExpressionPointcut);
		advisor.setAdvice(txAdvice);
		advisor.setOrder(0);
		return advisor;
	}
}