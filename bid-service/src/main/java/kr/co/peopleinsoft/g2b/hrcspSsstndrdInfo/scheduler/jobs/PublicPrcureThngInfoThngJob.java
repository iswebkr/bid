package kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.scheduler.jobs;

import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.controller.HrcspSsstndrdInfoController;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.service.HrcspSsstndrdInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class PublicPrcureThngInfoThngJob extends HrcspSsstndrdInfoController implements Job {

	public PublicPrcureThngInfoThngJob(HrcspSsstndrdInfoService hrcspSsstndrdInfoService) {
		super(hrcspSsstndrdInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getPublicPrcureThngInfoThng();
	}
}