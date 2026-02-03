package kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler.jobs;

import kr.co.peopleinsoft.g2b.orderPlanSttus.controller.OrderPlanSttusController;
import kr.co.peopleinsoft.g2b.orderPlanSttus.service.OrderPlanSttusService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class OrderPlanSttusListFrgcptJob extends OrderPlanSttusController implements Job {

	public OrderPlanSttusListFrgcptJob(OrderPlanSttusService orderPlanSttusService) {
		super(orderPlanSttusService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		getOrderPlanSttusListFrgcpt();
	}
}