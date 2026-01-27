package kr.co.peopleinsoft.cmmn.cloud;

import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

public class CmmnLoadbalancerConfig {
	@Bean
	ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(ConfigurableApplicationContext applicationContext) {
		return ServiceInstanceListSupplier.builder()
			.withDiscoveryClient()
			.withCaching()
			.build(applicationContext);
	}
}