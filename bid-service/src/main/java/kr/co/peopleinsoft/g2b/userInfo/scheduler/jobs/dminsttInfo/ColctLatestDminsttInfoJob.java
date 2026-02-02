package kr.co.peopleinsoft.g2b.userInfo.scheduler.jobs.dminsttInfo;

import kr.co.peopleinsoft.g2b.userInfo.controller.DminsttInfoController;
import kr.co.peopleinsoft.g2b.userInfo.service.DminsttInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ColctLatestDminsttInfoJob extends DminsttInfoController implements Job {

	public ColctLatestDminsttInfoJob(DminsttInfoService dminsttInfoService) {
		super(dminsttInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		colctThisYearDminsttInfo();
	}
}