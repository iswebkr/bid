package kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler.jobs;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.orderPlanSttus.controller.OrderPlanSttusController;
import kr.co.peopleinsoft.g2b.orderPlanSttus.service.OrderPlanSttusService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class OrderPlanSttusListThngPPSSrchJob extends OrderPlanSttusController implements Job {

	public OrderPlanSttusListThngPPSSrchJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OrderPlanSttusService orderPlanSttusService) {
		super(publicWebClient, g2BCmmnService, orderPlanSttusService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			getOrderPlanSttusListThngPPSSrch();
		} catch (Exception ignore) {
		}
	}
}