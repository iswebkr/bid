package kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler.jobs;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.bidPublicInfo.controller.BidPublicInfoController;
import kr.co.peopleinsoft.g2b.bidPublicInfo.service.BidPublicInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class BidPblancListInfoThngPPSSrchJob extends BidPublicInfoController implements Job {

	public BidPblancListInfoThngPPSSrchJob(G2BCmmnService g2BCmmnService, WebClient publicWebClient, BidPublicInfoService bidPublicInfoService) {
		super(g2BCmmnService, publicWebClient, bidPublicInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			getBidPblancListInfoThngPPSSrch();
		} catch (Exception ignore) {
		}
	}
}