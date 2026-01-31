package kr.co.peopleinsoft.biz.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public abstract class CmmnAbstractController {

	private final static Logger logger = LoggerFactory.getLogger(CmmnAbstractController.class);

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
}