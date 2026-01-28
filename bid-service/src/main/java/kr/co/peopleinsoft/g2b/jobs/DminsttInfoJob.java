package kr.co.peopleinsoft.g2b.jobs;

import kr.co.peopleinsoft.g2b.controller.useInfo.DminsttInfoController;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.usrInfo.DminsttInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class DminsttInfoJob extends DminsttInfoController implements Job {

	public DminsttInfoJob(G2BCmmnService g2BCmmnService, WebClient publicWebClient, DminsttInfoService dminsttInfoService) {
		super(g2BCmmnService, publicWebClient, dminsttInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepDminsttInfo();
		} catch (Exception ignore) {
		}
	}
}