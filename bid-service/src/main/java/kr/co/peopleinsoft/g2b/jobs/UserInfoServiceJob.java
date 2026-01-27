package kr.co.peopleinsoft.g2b.jobs;

import kr.co.peopleinsoft.g2b.controller.useInfo.DminsttInfoController;
import kr.co.peopleinsoft.g2b.service.usrInfo.DminsttInfoService;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.schdul.BidSchdulHistManageService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class UserInfoServiceJob extends DminsttInfoController implements Job {
	public UserInfoServiceJob(G2BCmmnService g2BCmmnService, WebClient publicWebClient, DminsttInfoService g2BUserInfoService, BidSchdulHistManageService g2BSchdulHistManageService) {
		super(g2BCmmnService, publicWebClient, g2BUserInfoService, g2BSchdulHistManageService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepDminsttInfo();
		} catch (Exception ignore) {
		}
	}
}