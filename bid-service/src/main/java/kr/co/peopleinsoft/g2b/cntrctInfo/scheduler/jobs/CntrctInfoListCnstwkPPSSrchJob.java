package kr.co.peopleinsoft.g2b.cntrctInfo.scheduler.jobs;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.cntrctInfo.controller.CntrctInfoController;
import kr.co.peopleinsoft.g2b.cntrctInfo.service.CntrctInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

public class CntrctInfoListCnstwkPPSSrchJob extends CntrctInfoController implements Job {

	public CntrctInfoListCnstwkPPSSrchJob(G2BCmmnService g2BCmmnService, AsyncTaskExecutor asyncTaskExecutor, WebClient publicWebClient, CntrctInfoService cntrctInfoService) {
		super(g2BCmmnService, asyncTaskExecutor, publicWebClient, cntrctInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getCntrctInfoListCnstwkPPSSrch();
	}
}