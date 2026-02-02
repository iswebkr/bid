package kr.co.peopleinsoft.cmmn.controller;

import jakarta.annotation.Resource;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.dto.BidResponseDto;
import kr.co.peopleinsoft.cmmn.service.BidSchdulHistManageService;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class G2BAbstractBidController extends CmmnAbstractController {

	private final Logger logger = LoggerFactory.getLogger(G2BAbstractBidController.class);

	@Resource(name = "g2BCmmnService")
	protected G2BCmmnService g2BCmmnService;

	@Resource(name = "asyncTaskExecutor")
	protected AsyncTaskExecutor asyncTaskExecutor;

	@Resource(name = "publicWebClient")
	protected WebClient publicWebClient;

	@Resource(name = "bidSchdulHistManageService")
	protected BidSchdulHistManageService bidSchdulHistManageService;

	protected ResponseEntity<String> asyncProcess(Runnable runnable, TaskExecutor taskExecutor) {
		CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(runnable, taskExecutor)
			.orTimeout(3, TimeUnit.MINUTES)
			.exceptionallyAsync(ex -> {
				if (logger.isErrorEnabled()) {
					logger.error("비동기 작업 실패 : {}", ex.getMessage(), ex);
				}
				throw new CompletionException(ex);
			});

		return ResponseEntity.ok().build();
	}

	protected ResponseEntity<String> asyncParallelProcess(List<Runnable> runnables, TaskExecutor taskExecutor) {
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (Runnable runnable : runnables) {
			futures.add(CompletableFuture.runAsync(runnable, taskExecutor).orTimeout(3, TimeUnit.MINUTES));
		}

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

		return ResponseEntity.ok().build();
	}

	protected <T extends BidResponseDto> T getResponse(Class<T> clazz, URI uri) {
		return publicWebClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(clazz)
			.block();
	}
}