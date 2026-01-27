package kr.co.peopleinsoft.g2b.jobs;

import kr.co.peopleinsoft.g2b.controller.orderPlanSttus.OrderPlanSttusController;
import kr.co.peopleinsoft.g2b.service.orderPlanSttus.OrderPlanSttusService;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.schdul.BidSchdulHistManageService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class G2BOrderPlanSttusJob extends OrderPlanSttusController implements Job {

	public G2BOrderPlanSttusJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, OrderPlanSttusService g2bOrderPlanSttusService, BidSchdulHistManageService g2BSchdulHistManageService) {
		super(publicWebClient, g2BCmmnService, g2bOrderPlanSttusService, g2BSchdulHistManageService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepOrderPlanSttus();
		} catch (Exception ignore) {
		}
	}
}