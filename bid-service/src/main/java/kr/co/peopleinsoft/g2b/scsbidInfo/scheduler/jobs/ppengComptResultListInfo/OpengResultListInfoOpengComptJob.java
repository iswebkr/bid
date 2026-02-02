package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.ppengComptResultListInfo;

import kr.co.peopleinsoft.g2b.scsbidInfo.controller.OpengComptResultListInfoController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengComptResultListInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class OpengResultListInfoOpengComptJob extends OpengComptResultListInfoController implements Job {

	public OpengResultListInfoOpengComptJob(OpengComptResultListInfoService opengComptResultListInfoService) {
		super(opengComptResultListInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		saveStepOpengResultListInfoOpengCompt();
	}
}