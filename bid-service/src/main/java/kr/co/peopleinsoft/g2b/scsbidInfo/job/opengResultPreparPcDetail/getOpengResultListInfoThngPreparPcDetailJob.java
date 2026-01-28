package kr.co.peopleinsoft.g2b.scsbidInfo.job.opengResultPreparPcDetail;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.scsbidInfo.controller.OpengResultPreparPcDetailController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengResultPreparPcDetailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class getOpengResultListInfoThngPreparPcDetailJob extends OpengResultPreparPcDetailController implements Job {

	public getOpengResultListInfoThngPreparPcDetailJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OpengResultPreparPcDetailService opengResultPreparPcDetailService) {
		super(publicWebClient, g2BCmmnService, opengResultPreparPcDetailService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			getOpengResultListInfoThngPreparPcDetail();
		} catch (Exception ignore) {
		}
	}
}