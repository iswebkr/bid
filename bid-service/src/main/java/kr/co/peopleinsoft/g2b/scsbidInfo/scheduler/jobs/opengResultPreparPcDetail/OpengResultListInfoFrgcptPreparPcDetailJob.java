package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultPreparPcDetail;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.scsbidInfo.controller.OpengResultPreparPcDetailController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengResultPreparPcDetailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class OpengResultListInfoFrgcptPreparPcDetailJob extends OpengResultPreparPcDetailController implements Job {

	public OpengResultListInfoFrgcptPreparPcDetailJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OpengResultPreparPcDetailService opengResultPreparPcDetailService) {
		super(publicWebClient, g2BCmmnService, opengResultPreparPcDetailService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			getOpengResultListInfoFrgcptPreparPcDetail();
		} catch (Exception ignore) {
		}
	}
}