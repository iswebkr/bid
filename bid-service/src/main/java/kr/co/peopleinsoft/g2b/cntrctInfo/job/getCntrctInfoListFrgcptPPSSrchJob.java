package kr.co.peopleinsoft.g2b.cntrctInfo.job;

import kr.co.peopleinsoft.g2b.cntrctInfo.controller.CntrctInfoController;
import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.g2b.cntrctInfo.service.CntrctInfoService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class getCntrctInfoListFrgcptPPSSrchJob extends CntrctInfoController implements Job {

	public getCntrctInfoListFrgcptPPSSrchJob(G2BCmmnService g2BCmmnService, WebClient publicWebClient, CntrctInfoService cntrctInfoService) {
		super(g2BCmmnService, publicWebClient, cntrctInfoService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			getCntrctInfoListFrgcptPPSSrch();
		} catch (Exception ignore) {
		}
	}
}