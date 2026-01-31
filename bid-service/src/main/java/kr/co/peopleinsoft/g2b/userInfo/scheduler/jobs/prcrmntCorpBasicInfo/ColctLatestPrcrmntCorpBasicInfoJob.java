package kr.co.peopleinsoft.g2b.userInfo.scheduler.jobs.prcrmntCorpBasicInfo;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.userInfo.controller.PrcrmntCorpBasicInfoController;
import kr.co.peopleinsoft.g2b.userInfo.service.PrcrmntCorpBasicInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

public class ColctLatestPrcrmntCorpBasicInfoJob extends PrcrmntCorpBasicInfoController implements Job {

	public ColctLatestPrcrmntCorpBasicInfoJob(G2BCmmnService g2BCmmnService, AsyncTaskExecutor asyncTaskExecutor, WebClient publicWebClient, PrcrmntCorpBasicInfoService prcrmntCorpBasicInfoService) {
		super(g2BCmmnService, asyncTaskExecutor, publicWebClient, prcrmntCorpBasicInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		colctThisYearPrcrmntCorpBasicInfo();
	}
}