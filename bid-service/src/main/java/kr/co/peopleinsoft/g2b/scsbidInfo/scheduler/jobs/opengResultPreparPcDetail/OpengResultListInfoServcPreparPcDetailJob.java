package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultPreparPcDetail;

import kr.co.peopleinsoft.g2b.scsbidInfo.controller.OpengResultPreparPcDetailController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengResultPreparPcDetailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class OpengResultListInfoServcPreparPcDetailJob extends OpengResultPreparPcDetailController implements Job {

	public OpengResultListInfoServcPreparPcDetailJob(OpengResultPreparPcDetailService opengResultPreparPcDetailService) {
		super(opengResultPreparPcDetailService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getOpengResultListInfoServcPreparPcDetail();
	}
}