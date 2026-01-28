package kr.co.peopleinsoft.g2b.userInfo.job;

import kr.co.peopleinsoft.g2b.userInfo.controller.DminsttInfoController;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.userInfo.service.DminsttInfoService;
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