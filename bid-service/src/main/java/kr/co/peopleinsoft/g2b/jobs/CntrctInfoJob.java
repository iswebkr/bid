package kr.co.peopleinsoft.g2b.jobs;

import kr.co.peopleinsoft.g2b.controller.cntrctInfo.CntrctInfoController;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.cntrctInfo.CntrctInfoService;
import kr.co.peopleinsoft.g2b.service.schdul.BidSchdulHistManageService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class CntrctInfoJob extends CntrctInfoController implements Job {

	public CntrctInfoJob(G2BCmmnService g2BCmmnService, WebClient publicWebClient, CntrctInfoService cntrctInfoService, BidSchdulHistManageService bidSchdulHistManageService) {
		super(g2BCmmnService, publicWebClient, cntrctInfoService, bidSchdulHistManageService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepCntrctInfo();
		} catch (Exception ignore) {
		}
	}
}