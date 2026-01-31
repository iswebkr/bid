package kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler.jobs;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.orderPlanSttus.controller.OrderPlanSttusController;
import kr.co.peopleinsoft.g2b.orderPlanSttus.service.OrderPlanSttusService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

public class OrderPlanSttusListCnstwkPPSSrchJob extends OrderPlanSttusController implements Job {

	public OrderPlanSttusListCnstwkPPSSrchJob(WebClient publicWebClient, AsyncTaskExecutor asyncTaskExecutor, G2BCmmnService g2BCmmnService, OrderPlanSttusService orderPlanSttusService) {
		super(publicWebClient, asyncTaskExecutor, g2BCmmnService, orderPlanSttusService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getOrderPlanSttusListCnstwkPPSSrch();
	}
}