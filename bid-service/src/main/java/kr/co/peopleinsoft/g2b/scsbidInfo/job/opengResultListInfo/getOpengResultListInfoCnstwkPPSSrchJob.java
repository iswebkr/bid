package kr.co.peopleinsoft.g2b.scsbidInfo.job.opengResultListInfo;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.scsbidInfo.controller.OpengResultListInfoController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengResultListInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class getOpengResultListInfoCnstwkPPSSrchJob extends OpengResultListInfoController implements Job {

	public getOpengResultListInfoCnstwkPPSSrchJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OpengResultListInfoService opengResultListInfoService) {
		super(publicWebClient, g2BCmmnService, opengResultListInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			getOpengResultListInfoCnstwkPPSSrch();
		} catch (Exception ignore) {
		}
	}
}