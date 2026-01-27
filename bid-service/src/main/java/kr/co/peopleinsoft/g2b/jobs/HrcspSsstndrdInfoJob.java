package kr.co.peopleinsoft.g2b.jobs;

import kr.co.peopleinsoft.g2b.controller.hrcspSsstndrdInfo.HrcspSsstndrdInfoController;
import kr.co.peopleinsoft.g2b.service.hrcspSsstndrdInfo.HrcspSsstndrdInfoService;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.schdul.BidSchdulHistManageService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class HrcspSsstndrdInfoJob extends HrcspSsstndrdInfoController implements Job {

	public HrcspSsstndrdInfoJob(WebClient publicWebClient, G2BCmmnService g2BCmmnService, HrcspSsstndrdInfoService g2BHrcspSsstndrdInfoService, BidSchdulHistManageService g2BSchdulHistManageService) {
		super(publicWebClient, g2BCmmnService, g2BHrcspSsstndrdInfoService, g2BSchdulHistManageService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepHrcspSsstndrdInfo();
		} catch (Exception ignore) {
		}
	}
}