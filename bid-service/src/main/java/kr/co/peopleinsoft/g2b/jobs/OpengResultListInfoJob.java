package kr.co.peopleinsoft.g2b.jobs;

import kr.co.peopleinsoft.g2b.controller.scsbidInfo.OpengResultListInfoController;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.scsbidInfo.OpengResultListInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class OpengResultListInfoJob extends OpengResultListInfoController implements Job {

	public OpengResultListInfoJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OpengResultListInfoService opengResultListInfoService) {
		super(publicWebClient, g2BCmmnService, opengResultListInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepScsbidListSttus();
		} catch (Exception ignore) {
		}
	}
}