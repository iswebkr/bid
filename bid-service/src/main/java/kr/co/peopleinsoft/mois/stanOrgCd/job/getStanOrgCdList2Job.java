package kr.co.peopleinsoft.mois.stanOrgCd.job;

import kr.co.peopleinsoft.cmmn.service.G2BCmmnService;
import kr.co.peopleinsoft.mois.stanOrgCd.controller.StanOrgCdController;
import kr.co.peopleinsoft.mois.stanOrgCd.service.StanOrgCdService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.web.reactive.function.client.WebClient;

public class getStanOrgCdList2Job extends StanOrgCdController implements Job {

	public getStanOrgCdList2Job(G2BCmmnService g2BCmmnService, WebClient publicWebClient, StanOrgCdService stanOrgCdService) {
		super(g2BCmmnService, publicWebClient, stanOrgCdService);
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			getStanOrgCdList2();
		} catch (Exception ignore) {
		}
	}
}