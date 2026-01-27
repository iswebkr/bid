package kr.co.peopleinsoft.cmmn.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CmmnCaffeinCacheConfig {

	static final Logger logger = LoggerFactory.getLogger(CmmnCaffeinCacheConfig.class);

	@Bean
	Caffeine<Object, Object> caffeine() {
		return Caffeine.newBuilder()
			.initialCapacity(100)
			.maximumSize(150)
			.expireAfterAccess(5, TimeUnit.MINUTES)
			.weakKeys()
			.expireAfterWrite(60, TimeUnit.MINUTES)
			.removalListener((key, value, cause) -> {
				if (logger.isInfoEnabled()) {
					logger.info("removal listener called with key [{}], value [{}], cause [{}], evicted [{}]", key, value, cause, cause.wasEvicted());
				}
			})
			.recordStats();
	}

	@Bean
	CaffeineCacheManager cacheManager() {
		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
		caffeineCacheManager.setAllowNullValues(false);
		caffeineCacheManager.setCaffeine(caffeine());
		return caffeineCacheManager;
	}
}