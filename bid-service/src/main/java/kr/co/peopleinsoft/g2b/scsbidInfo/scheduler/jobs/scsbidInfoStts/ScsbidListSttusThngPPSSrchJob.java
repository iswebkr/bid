package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.scsbidInfoStts;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.scsbidInfo.controller.ScsbidInfoSttsController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.ScsbidInfoSttsService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

public class ScsbidListSttusThngPPSSrchJob extends ScsbidInfoSttsController implements Job {

	public ScsbidListSttusThngPPSSrchJob(WebClient publicWebClient, AsyncTaskExecutor asyncTaskExecutor, G2BCmmnService g2BCmmnService, ScsbidInfoSttsService scsbidInfoSttsService) {
		super(publicWebClient, asyncTaskExecutor, g2BCmmnService, scsbidInfoSttsService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getScsbidListSttusThngPPSSrch();
	}
}