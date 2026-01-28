package kr.co.peopleinsoft.g2b.jobs.bidPublicInfo;

import kr.co.peopleinsoft.g2b.controller.bidPublicInfo.BidPublicInfoController;
import kr.co.peopleinsoft.g2b.service.bidPublicInfo.BidPublicInfoService;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class getBidPblancListInfoThngPPSSrchJob extends BidPublicInfoController implements Job {

	public getBidPblancListInfoThngPPSSrchJob(G2BCmmnService g2BCmmnService, WebClient publicWebClient, BidPublicInfoService bidPublicInfoService) {
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