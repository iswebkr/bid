package kr.co.peopleinsoft.g2b.cntrctInfo.scheduler.jobs;

import kr.co.peopleinsoft.g2b.cntrctInfo.controller.CntrctInfoController;
import kr.co.peopleinsoft.g2b.cntrctInfo.service.CntrctInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CntrctInfoListFrgcptJob extends CntrctInfoController implements Job {

	public CntrctInfoListFrgcptJob(CntrctInfoService cntrctInfoService) {
		super(cntrctInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getCntrctInfoListFrgcpt();
	}
}