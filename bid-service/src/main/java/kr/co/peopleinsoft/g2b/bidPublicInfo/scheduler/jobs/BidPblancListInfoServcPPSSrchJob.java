package kr.co.peopleinsoft.g2b.bidPublicInfo.scheduler.jobs;

import kr.co.peopleinsoft.g2b.bidPublicInfo.controller.BidPublicInfoController;
import kr.co.peopleinsoft.g2b.bidPublicInfo.service.BidPublicInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class BidPblancListInfoServcPPSSrchJob extends BidPublicInfoController implements Job {

	public BidPblancListInfoServcPPSSrchJob(BidPublicInfoService bidPublicInfoService) {
		super(bidPublicInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getBidPblancListInfoServc();
	}
}