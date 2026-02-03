package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultListInfo.ColctLatestOpengResultListInfoJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultListInfo.OpengResultListInfoCnstwkJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultListInfo.OpengResultListInfoFrgcptJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultListInfo.OpengResultListInfoServcJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultListInfo.OpengResultListInfoThngJob;
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
public class OpengResultListInfoScheduler {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public OpengResultListInfoScheduler(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "개찰결과 공사 목록 조회", description = "개찰결과 공사 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/opengResultListInfo/OpengResultListInfoCnstwkJob")
	public ResponseEntity<String> OpengResultListInfoCnstwkJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 2 * * ?");
		cmmnScheduleManager.deleteJob("OpengResultListInfoCnstwkJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengResultListInfoCnstwkJob.class, "OpengResultListInfoCnstwkJob", "낙찰정보", "개찰결과 공사 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "개찰결과 용역 조회", description = "개찰결과 용역 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/opengResultListInfo/OpengResultListInfoServcJob")
	public ResponseEntity<String> OpengResultListInfoServcJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 2 * * ?");
		cmmnScheduleManager.deleteJob("OpengResultListInfoServcJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengResultListInfoServcJob.class, "OpengResultListInfoServcJob", "낙찰정보", "개찰결과 용역 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "개찰결과 외자 조회", description = "개찰결과 외자 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/opengResultListInfo/OpengResultListInfoFrgcptJob")
	public ResponseEntity<String> OpengResultListInfoFrgcptJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 2 * * ?");
		cmmnScheduleManager.deleteJob("OpengResultListInfoFrgcptJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengResultListInfoFrgcptJob.class, "OpengResultListInfoFrgcptJob", "낙찰정보", "개찰결과 외자 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "개찰결과 물품 조회", description = "개찰결과 물품 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/opengResultListInfo/OpengResultListInfoThngJob")
	public ResponseEntity<String> OpengResultListInfoThngJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 2 * * ?");
		cmmnScheduleManager.deleteJob("OpengResultListInfoThngJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengResultListInfoThngJob.class, "OpengResultListInfoThngJob", "낙찰정보", "개찰결과 물품 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/***************************** 낙찰정보 - 개찰결과(최신데이터) *****************************/

	@Operation(summary = "개찰결과 최신데이터 수집", description = "개찰결과 최신데이터 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/latest/opengResultListInfo/colctLatestOpengResultListInfoJob")
	public ResponseEntity<String> colctLatestOpengResultListInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 */10 * * * ?");
		cmmnScheduleManager.deleteJob("ColctLatestOpengResultListInfoJob", "최신자료수집"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ColctLatestOpengResultListInfoJob.class, "ColctLatestOpengResultListInfoJob", "최신자료수집", "개찰결과 최신데이터 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}