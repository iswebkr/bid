package kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.scheduler.jobs;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.controller.HrcspSsstndrdInfoController;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.service.HrcspSsstndrdInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class PublicPrcureThngInfoFrgcptPPSSrchJob extends HrcspSsstndrdInfoController implements Job {

	public PublicPrcureThngInfoFrgcptPPSSrchJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, HrcspSsstndrdInfoService hrcspSsstndrdInfoService) {
		super(publicWebClient, g2BCmmnService, hrcspSsstndrdInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			getPublicPrcureThngInfoFrgcptPPSSrch();
		} catch (Exception ignore) {
		}
	}
}