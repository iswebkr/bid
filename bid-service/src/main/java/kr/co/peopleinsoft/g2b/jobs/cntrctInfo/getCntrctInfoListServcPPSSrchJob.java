package kr.co.peopleinsoft.g2b.jobs.cntrctInfo;

import kr.co.peopleinsoft.g2b.controller.cntrctInfo.CntrctInfoController;
import kr.co.peopleinsoft.g2b.service.cmmn.G2BCmmnService;
import kr.co.peopleinsoft.g2b.service.cntrctInfo.CntrctInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class getCntrctInfoListServcPPSSrchJob extends CntrctInfoController implements Job {

	public getCntrctInfoListServcPPSSrchJob(G2BCmmnService g2BCmmnService, WebClient publicWebClient, CntrctInfoService cntrctInfoService) {
		super(g2BCmmnService, publicWebClient, cntrctInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			getCntrctInfoListServcPPSSrch();
		} catch (Exception ignore) {
		}
	}
}