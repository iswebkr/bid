package kr.co.peopleinsoft.cmmn.aspect;

import kr.co.peopleinsoft.cmmn.datasource.router.DataSourceContextHolder;
import kr.co.peopleinsoft.cmmn.datasource.router.DataSourceType;
import kr.co.peopleinsoft.cmmn.datasource.router.RoutingDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

@Component
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CmmnServicesAspect {

	private static final Logger logger = LoggerFactory.getLogger(CmmnServicesAspect.class);

	// @Around("@within(org.springframework.stereotype.Service)")
	@Around("@annotation(kr.co.peopleinsoft.cmmn.datasource.router.RoutingDataSource) || @within(kr.co.peopleinsoft.cmmn.datasource.router.RoutingDataSource)")
	public Object doServiceAround(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		Class<?> targetClass = joinPoint.getTarget().getClass();

		String methodName = targetClass.getSimpleName() + "." + method.getName();

		// 이전 컨텍스트의 DataSource 설정
		DataSourceType previousDataSourceType = DataSourceContextHolder.getDataSourceType();

		// 신규 컨텍스트의 DataSource 설정
		DataSourceType newDataSource = getDataSourceType(method, targetClass);

		// 이전 켄텍스트 DataSource에서 신규DataSource로 변경된 경우 처리 (초기값 : false)
		boolean contextChanged = false;

		// 성능 측정
		StopWatch stopWatch = new StopWatch(method.getName());
		stopWatch.start();

		try {
			if (newDataSource != previousDataSourceType) {
				DataSourceContextHolder.setDataSourceType(newDataSource);
				contextChanged = true;
			}
			Object result = joinPoint.proceed();

			// 트랜잭션 정보는 TRACE 레벨로
			if (logger.isTraceEnabled()) {
				logger.trace("Transaction Name: {}", TransactionSynchronizationManager.getCurrentTransactionName());
				logger.trace("Transaction Active: {}", TransactionSynchronizationManager.isActualTransactionActive());
				logger.trace("Transaction ReadOnly: {}", TransactionSynchronizationManager.isCurrentTransactionReadOnly());
			}

			return result;
		} catch (Exception e) {
			logger.error("메서드 실행 중 예외 발생: {} - {}", methodName, e.getMessage());
			throw e;
		} finally {
			stopWatch.stop();

			if (logger.isDebugEnabled()) {
				logger.debug("메서드 실행 시간: {} ({} ms)", methodName, stopWatch.getTotalTimeMillis());
			}

			if (contextChanged) {
				if(previousDataSourceType != null) {
					DataSourceContextHolder.setDataSourceType(previousDataSourceType);
					if (logger.isDebugEnabled()) {
						logger.debug("DataSource 복원: {} -> [{}]로 복원", methodName, previousDataSourceType);
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("DataSource 해제: {}", methodName);
					}
					DataSourceContextHolder.clearDataSourceType();
				}
			}
		}
	}

	private DataSourceType getDataSourceType(Method method, Class<?> targetClass) {
		// 1. 메서드 레벨 어노테이션 확인
		RoutingDataSource methodAnnotation = method.getAnnotation(RoutingDataSource.class);
		if (methodAnnotation != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("메서드 레벨 @RoutingDataSource 발견: {} -> {}",
					method.getName(), methodAnnotation.value());
			}
			return methodAnnotation.value();
		}

		// 2. 클래스 레벨 어노테이션 확인
		RoutingDataSource classAnnotation = targetClass.getAnnotation(RoutingDataSource.class);
		if (classAnnotation != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("클래스 레벨 @RoutingDataSource 발견: {} -> {}",
					targetClass.getSimpleName(), classAnnotation.value());
			}
			return classAnnotation.value();
		}

		// 3. 어노테이션이 없는 경우 현재 DataSource 유지
		// (이 경우는 @within 포인트컷으로 인해 발생 가능)
		if (logger.isTraceEnabled()) {
			logger.trace("@RoutingDataSource 미발견 -> 현재 DataSource 유지");
		}
		return DataSourceContextHolder.getDataSourceType();
	}
}