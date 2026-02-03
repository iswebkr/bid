package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultListInfo;

import kr.co.peopleinsoft.g2b.scsbidInfo.controller.OpengResultListInfoController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengResultListInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class OpengResultListInfoFrgcptJob extends OpengResultListInfoController implements Job {

	public OpengResultListInfoFrgcptJob(OpengResultListInfoService opengResultListInfoService) {
		super(opengResultListInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getOpengResultListInfoFrgcpt();
	}
}