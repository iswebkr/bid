package kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler.jobs;

import kr.co.peopleinsoft.g2b.orderPlanSttus.controller.OrderPlanSttusController;
import kr.co.peopleinsoft.g2b.orderPlanSttus.service.OrderPlanSttusService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CollectionTodayAndYesterdayDataJob extends OrderPlanSttusController implements Job {

	public CollectionTodayAndYesterdayDataJob(OrderPlanSttusService orderPlanSttusService) {
		super(orderPlanSttusService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		collectionTodayAndYesterdayData();
	}
}