package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultListInfo;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.scsbidInfo.controller.OpengResultListInfoController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengResultListInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

public class ColctLatestOpengResultListInfoJob extends OpengResultListInfoController implements Job {

	public ColctLatestOpengResultListInfoJob(WebClient publicWebClient, AsyncTaskExecutor asyncTaskExecutor, G2BCmmnService g2BCmmnService, OpengResultListInfoService opengResultListInfoService) {
		super(publicWebClient, asyncTaskExecutor, g2BCmmnService, opengResultListInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		colctThisYearOpengResultListInfo();
	}
}