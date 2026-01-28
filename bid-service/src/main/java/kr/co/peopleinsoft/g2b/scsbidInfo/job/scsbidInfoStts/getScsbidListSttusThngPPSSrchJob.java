package kr.co.peopleinsoft.g2b.scsbidInfo.job.scsbidInfoStts;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.scsbidInfo.controller.ScsbidInfoSttsController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.ScsbidInfoSttsService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class getScsbidListSttusThngPPSSrchJob extends ScsbidInfoSttsController implements Job {

	public getScsbidListSttusThngPPSSrchJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, ScsbidInfoSttsService scsbidInfoSttsService) {
		super(publicWebClient, g2BCmmnService, scsbidInfoSttsService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			getScsbidListSttusThngPPSSrch();
		} catch (Exception ignore) {
		}
	}
}