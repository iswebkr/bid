package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.ppengComptResultListInfo;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.scsbidInfo.controller.OpengComptResultListInfoController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengComptResultListInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

public class ColctLatestOpengResultListInfoOpengComptJob extends OpengComptResultListInfoController implements Job {

	public ColctLatestOpengResultListInfoOpengComptJob(WebClient publicWebClient, AsyncTaskExecutor asyncTaskExecutor, G2BCmmnService g2BCmmnService, OpengComptResultListInfoService opengComptResultListInfoService) {
		super(publicWebClient, asyncTaskExecutor, g2BCmmnService, opengComptResultListInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		colctThisYearOpengResultListInfoOpengCompt();
	}
}