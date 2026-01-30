package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.ppengComptResultListInfo.ColctLatestOpengResultListInfoOpengComptJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.ppengComptResultListInfo.OpengResultListInfoOpengComptJob;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@RequestMapping("/scheduler/ScsbidInfoService")
@Tag(name = "나라장터 낙찰정보서비스(ScsbidInfoService)", description = "나라장터 개찰결과를 물품, 공사, 용역, 외자의 업무별로 제공하는 서비스로 각 업무별로 최종낙찰자, 개찰순위, 복수예비가 및 예비가격 정보를 제공하며 개찰완료목록, 재입찰목록, 유찰목록 또한 제공하는 나라장터 낙찰정보서비스")
public class OpengComptResultListInfoScheduler {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public OpengComptResultListInfoScheduler(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "낙찰정보 - 개찰결과 개찰완료", description = "낙찰정보 - 개찰결과 개찰완료", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/ppengComptResultListInfo/ppengResultListInfoOpengComptJob")
	public ResponseEntity<String> ppengResultListInfoOpengComptJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 1 * * ?");
		cmmnScheduleManager.deleteJob("OpengResultListInfoOpengComptJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengResultListInfoOpengComptJob.class, "OpengResultListInfoOpengComptJob", "낙찰정보", "개찰결과 개찰완료", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/***************************** 낙찰정보 - 개찰결과*개찰완료(최신데이터) *****************************/

	@Operation(summary = "낙찰정보 - 개찰결과 개찰완료 최신데이터 수집", description = "낙찰정보 - 개찰결과 개찰완료 최신데이터 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/latest/ppengComptResultListInfo/colctLatestOpengResultListInfoOpengComptJob")
	public ResponseEntity<String> colctLatestOpengResultListInfoOpengComptJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 */10 * * * ?");
		cmmnScheduleManager.deleteJob("ColctLatestOpengResultListInfoOpengComptJob", "최신자료수집"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ColctLatestOpengResultListInfoOpengComptJob.class, "ColctLatestOpengResultListInfoOpengComptJob", "최신자료수집", "낙찰정보 - 개찰결과 개찰완료 최신데이터 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}