package kr.co.peopleinsoft.g2b.userInfo.job;

import kr.co.peopleinsoft.g2b.userInfo.controller.PrcrmntCorpBasicInfoController;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.cmmn.service.BidSchdulHistManageService;
import kr.co.peopleinsoft.g2b.userInfo.service.PrcrmntCorpBasicInfoService;
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