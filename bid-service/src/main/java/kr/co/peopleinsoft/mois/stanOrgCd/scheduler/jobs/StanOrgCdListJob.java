package kr.co.peopleinsoft.mois.stanOrgCd.scheduler.jobs;

import kr.co.peopleinsoft.mois.stanOrgCd.controller.StanOrgCdController;
import kr.co.peopleinsoft.mois.stanOrgCd.service.StanOrgCdService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class StanOrgCdListJob extends StanOrgCdController implements Job {

	public StanOrgCdListJob(StanOrgCdService stanOrgCdService) {
		super(stanOrgCdService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getStanOrgCdList2();
	}
}