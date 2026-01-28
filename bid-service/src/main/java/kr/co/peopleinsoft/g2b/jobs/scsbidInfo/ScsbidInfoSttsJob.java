package kr.co.peopleinsoft.g2b.jobs.scsbidInfo;

import kr.co.peopleinsoft.g2b.controller.scsbidInfo.ScsbidInfoSttsController;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.scsbidInfo.ScsbidInfoSttsService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class ScsbidInfoSttsJob extends ScsbidInfoSttsController implements Job {

	public ScsbidInfoSttsJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, ScsbidInfoSttsService scsbidInfoSttsService) {
		super(publicWebClient, g2BCmmnService, scsbidInfoSttsService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepScsbidInfo();
		} catch (Exception ignore) {
		}
	}
}