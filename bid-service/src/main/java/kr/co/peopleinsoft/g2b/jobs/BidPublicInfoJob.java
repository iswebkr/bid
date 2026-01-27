package kr.co.peopleinsoft.g2b.jobs;

import kr.co.peopleinsoft.g2b.controller.bidPublicInfo.BidPublicInfoController;
import kr.co.peopleinsoft.g2b.service.bidPublicInfo.BidPublicInfoService;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.schdul.BidSchdulHistManageService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class BidPublicInfoJob extends BidPublicInfoController implements Job {

	public BidPublicInfoJob(G2BCmmnService g2BCmmnService, WebClient publicWebClient, BidPublicInfoService g2BBidPublicInfoService, BidSchdulHistManageService g2BSchdulHistManageService) {
		super(g2BCmmnService, publicWebClient, g2BBidPublicInfoService, g2BSchdulHistManageService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepBidPublicInfo();
		} catch (Exception ignore) {
		}
	}
}