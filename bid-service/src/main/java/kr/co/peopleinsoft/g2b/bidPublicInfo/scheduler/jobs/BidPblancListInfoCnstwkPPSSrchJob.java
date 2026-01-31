package kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler.jobs;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.bidPublicInfo.controller.BidPublicInfoController;
import kr.co.peopleinsoft.g2b.bidPublicInfo.service.BidPublicInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

public class BidPblancListInfoCnstwkPPSSrchJob extends BidPublicInfoController implements Job {

	public BidPblancListInfoCnstwkPPSSrchJob(G2BCmmnService g2BCmmnService, AsyncTaskExecutor asyncTaskExecutor, WebClient publicWebClient, BidPublicInfoService bidPublicInfoService) {
		super(g2BCmmnService, asyncTaskExecutor, publicWebClient, bidPublicInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getBidPblancListInfoCnstwkPPSSrch();
	}
}