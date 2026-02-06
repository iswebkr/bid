package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.opengResultPreparPcDetail.job;

import kr.co.peopleinsoft.g2b.scsbidInfo.controller.OpengResultPreparPcDetailController;
import kr.co.peopleinsoft.g2b.scsbidInfo.service.OpengResultPreparPcDetailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CollectionLastFiveYearDataJob extends OpengResultPreparPcDetailController implements Job {

	public CollectionLastFiveYearDataJob(OpengResultPreparPcDetailService opengResultPreparPcDetailService) {
		super(opengResultPreparPcDetailService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		collectionLastFiveYearData();
	}
}