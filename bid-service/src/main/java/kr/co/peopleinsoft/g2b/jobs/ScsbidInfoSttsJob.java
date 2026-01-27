package kr.co.peopleinsoft.g2b.jobs;

import kr.co.peopleinsoft.g2b.controller.scsbidInfo.ScsbidInfoSttsController;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.schdul.BidSchdulHistManageService;
import kr.co.peopleinsoft.g2b.service.scsbidInfo.ScsbidInfoSttsService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutionException;

public class ScsbidInfoSttsJob extends ScsbidInfoSttsController implements Job {

	public ScsbidInfoSttsJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, ScsbidInfoSttsService scsbidInfoSttsService, BidSchdulHistManageService schdulHistManageService) {
		super(publicWebClient, g2BCmmnService, scsbidInfoSttsService, schdulHistManageService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepScsbidInfo();
		} catch (Exception ignore) {
		}
	}
}