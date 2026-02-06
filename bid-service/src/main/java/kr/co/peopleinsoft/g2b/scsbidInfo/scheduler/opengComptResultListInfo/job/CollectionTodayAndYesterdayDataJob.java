package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.opengComptResultListInfo.job;

import kr.co.peopleinsoft.g2b.scsbidInfo.controller.OpengComptResultListInfoController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengComptResultListInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CollectionTodayAndYesterdayDataJob extends OpengComptResultListInfoController implements Job {

	public CollectionTodayAndYesterdayDataJob(OpengComptResultListInfoService opengComptResultListInfoService) {
		super(opengComptResultListInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		collectionTodayAndYesterdayData();
	}
}