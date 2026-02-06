package kr.co.peopleinsoft.g2b.userInfo.scheduler.prcrmntCorpBasicInfo.job;

import kr.co.peopleinsoft.g2b.userInfo.controller.PrcrmntCorpBasicInfoController;
import kr.co.peopleinsoft.g2b.userInfo.service.PrcrmntCorpBasicInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CollectionLastFiveYearDataJob extends PrcrmntCorpBasicInfoController implements Job {

	public CollectionLastFiveYearDataJob(PrcrmntCorpBasicInfoService prcrmntCorpBasicInfoService) {
		super(prcrmntCorpBasicInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		collectionLastFiveYearData();
	}
}