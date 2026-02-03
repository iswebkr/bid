package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.scsbidInfoStts;

import kr.co.peopleinsoft.g2b.scsbidInfo.controller.ScsbidInfoSttsController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.ScsbidInfoSttsService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScsbidListSttusFrgcptJob extends ScsbidInfoSttsController implements Job {

	public ScsbidListSttusFrgcptJob(ScsbidInfoSttsService scsbidInfoSttsService) {
		super(scsbidInfoSttsService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getScsbidListSttusFrgcpt();
	}
}