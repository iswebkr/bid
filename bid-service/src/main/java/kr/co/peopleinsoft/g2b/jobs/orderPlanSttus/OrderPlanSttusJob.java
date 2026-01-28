package kr.co.peopleinsoft.g2b.jobs.orderPlanSttus;

import kr.co.peopleinsoft.g2b.controller.orderPlanSttus.OrderPlanSttusController;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.orderPlanSttus.OrderPlanSttusService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class OrderPlanSttusJob extends OrderPlanSttusController implements Job {

	public OrderPlanSttusJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OrderPlanSttusService orderPlanSttusService) {
		super(publicWebClient, g2BCmmnService, orderPlanSttusService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepOrderPlanSttus();
		} catch (Exception ignore) {
		}
	}
}