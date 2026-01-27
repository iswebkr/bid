package kr.co.peopleinsoft.g2b.jobs;

import kr.co.peopleinsoft.g2b.controller.useInfo.PrcrmntCorpBasicInfoController;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.schdul.BidSchdulHistManageService;
import kr.co.peopleinsoft.g2b.service.usrInfo.PrcrmntCorpBasicInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class PrcrmntCorpBasicInfoJob extends PrcrmntCorpBasicInfoController implements Job {

	public PrcrmntCorpBasicInfoJob(G2BCmmnService g2BCmmnService, WebClient publicWebClient, PrcrmntCorpBasicInfoService prcrmntCorpBasicInfoService, BidSchdulHistManageService g2BSchdulHistManageService) {
		super(g2BCmmnService, publicWebClient, prcrmntCorpBasicInfoService, g2BSchdulHistManageService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			saveStepPrcrmntCorpBasicInfo();
		} catch (Exception ignore) {
		}
	}
}