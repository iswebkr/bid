package kr.co.peopleinsoft.mois.stanOrgCd.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.mois.stanOrgCd.scheduler.jobs.StanOrgCdListJob;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@RequestMapping("/mois/scheduler")
@Tag(name = "행정안전부 행정표준코드 기관코드", description = "행정표준코드 기관코드는 행정표준코드관리시스템(https://www.code.go.kr)에서 현재 제공 중인 기관코드(현존기관만 제공)를 제공하는 API입니다.")
public class StanOrgCdListScheduler {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public StanOrgCdListScheduler(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "행정안전부_행정표준코드_기관코드", description = "행정안전부_행정표준코드_기관코드", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/stanOrgCdList2Job")
	public ResponseEntity<String> stanOrgCdList2Job(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 30 18 * * MON");
		cmmnScheduleManager.deleteJob("StanOrgCdList2Job", "mois"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(StanOrgCdListJob.class, "StanOrgCdList2Job", "행정안전부", "행정안전부_행정표준코드_기관코드", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}