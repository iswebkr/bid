package kr.co.peopleinsoft.g2b.jobs.scsbidInfo;

import kr.co.peopleinsoft.g2b.controller.scsbidInfo.OpengComptResultListInfoController;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.scsbidInfo.OpengComptResultListInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class OpengComptResultListInfoJob extends OpengComptResultListInfoController implements Job {

	public OpengComptResultListInfoJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OpengComptResultListInfoService opengComptResultListInfoService) {
		super(publicWebClient, g2BCmmnService, opengComptResultListInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepOpengResultListInfoOpengCompt();
		} catch (Exception ignore) {
		}
	}
}