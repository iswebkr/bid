package kr.co.peopleinsoft.g2b.jobs;

import kr.co.peopleinsoft.g2b.controller.scsbidInfo.OpengResultPreparPcDetailController;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.schdul.BidSchdulHistManageService;
import kr.co.peopleinsoft.g2b.service.scsbidInfo.OpengResultPreparPcDetailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class OpengResultPreparPcDetailJob extends OpengResultPreparPcDetailController implements Job {

	public OpengResultPreparPcDetailJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OpengResultPreparPcDetailService opengResultPreparPcDetailService, BidSchdulHistManageService schdulHistManageService) {
		super(publicWebClient, g2BCmmnService, opengResultPreparPcDetailService, schdulHistManageService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepOpengResultPreparPcDetailInfo();
		} catch (Exception ignore) {
		}
	}
}