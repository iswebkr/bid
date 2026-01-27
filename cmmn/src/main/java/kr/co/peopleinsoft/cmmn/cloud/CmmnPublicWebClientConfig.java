package kr.co.peopleinsoft.cmmn.cloud;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.apache.hc.core5.http.ssl.TLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class CmmnPublicWebClientConfig {

	static final Logger logger = LoggerFactory.getLogger(CmmnPublicWebClientConfig.class);

	@Bean
	WebClient publicWebClient() {
		return publicWebClientBuilder().build();
	}

	WebClient.Builder publicWebClientBuilder() {
		return WebClient.builder()
			.clientConnector(httpClientConnector())
			.exchangeStrategies(ExchangeStrategies.builder()
				.codecs(configurer -> configurer
					.defaultCodecs()
					.maxInMemorySize(32 * 1024 * 1024))
				.build())
			.filter(ExchangeFilterFunction.ofRequestProcessor(request -> {
				if (logger.isDebugEnabled()) {
					logger.debug("Request: {} {}", request.method(), request.url());
				}
				return Mono.just(request);
			}))
			.filter(ExchangeFilterFunction.ofResponseProcessor(response -> {
				if (logger.isDebugEnabled()) {
					logger.debug("Response code : {}", response.statusCode());
				}
				return Mono.just(response);
			}))
			.filter(ExchangeFilterFunction.ofResponseProcessor(response -> {
				if (logger.isErrorEnabled()) {
					if (response.statusCode().isError()) {
						logger.error("Http Error : {}", response.statusCode());
					}
				}
				return Mono.just(response);
			}));
	}

	ConnectionProvider connectionProvider() {
		return ConnectionProvider.builder("connection-provider client connection pool")
			.maxConnections(20) // 최대 연결 수
			.maxConnectionPools(5) // connection pool 수
			.maxIdleTime(Duration.ofSeconds(30)) // 유휴 연결 유지 시간
			.maxLifeTime(Duration.ofSeconds(60)) // 연결 최대 생존 시간
			.pendingAcquireTimeout(Duration.ofSeconds(30)) // 연결 대기 시간
			.pendingAcquireMaxCount(50)  // 연결 대기 갯수
			.evictInBackground(Duration.ofSeconds(120)) //백그라운드 정리 주기
			.lifo()
			.build();
	}

	ReactorClientHttpConnector httpClientConnector() {
		HttpClient httpClient = HttpClient.create(connectionProvider())
			.protocol(HttpProtocol.H2, HttpProtocol.HTTP11)
			.resolver(DefaultAddressResolverGroup.INSTANCE)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000) // 연결 타임 아웃
			.option(ChannelOption.SO_KEEPALIVE, true) // Keep-Alive 활성화
			.option(ChannelOption.TCP_NODELAY, true) // Nagle 알고리즘 비활성화
			.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			.responseTimeout(Duration.ofSeconds(30)) // 응답 타임아웃
			.doOnConnected(connection -> {
				connection
					.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS)) // 읽기 타임아웃
					.addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)); // 쓰기 타임아웃
			})
			// .wiretap(true) // 네트워크로깅활성화
			.secure(sslContextSpec -> {
				try {
					sslContextSpec.sslContext(SslContextBuilder
						.forClient()
						.trustManager(InsecureTrustManagerFactory.INSTANCE)
						.protocols(TLS.V_1_2.getId(), TLS.V_1_3.getId())
						.build());
				} catch (SSLException e) {
					if (logger.isErrorEnabled()) {
						logger.error("SSL Context creation failed");
						throw new RuntimeException("SSL settings failed");
					}
				}
			}).doOnError((httpClientRequest, requestError) -> {
				if (logger.isErrorEnabled()) {
					logger.error("Error while calling sample service : {}", requestError.getMessage());
				}
			}, (httpClientResponse, responseError) -> {
				if (logger.isErrorEnabled()) {
					logger.error("Error while calling sample service : {}", responseError.getMessage());
				}
			});

		return new ReactorClientHttpConnector(httpClient);
	}
}