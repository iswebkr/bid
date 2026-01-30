package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultPreparPcDetail.ColctLatestOpengResultPreparPcDetailInfoJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultPreparPcDetail.OpengResultListInfoCnstwkPreparPcDetailJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultPreparPcDetail.OpengResultListInfoFrgcptPreparPcDetailJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultPreparPcDetail.OpengResultListInfoServcPreparPcDetailJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.opengResultPreparPcDetail.OpengResultListInfoThngPreparPcDetailJob;
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
public class OpengResultPreparPcDetailScheduler {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public OpengResultPreparPcDetailScheduler(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "낙찰정보 - 개찰결과 공사 예비가격상세 목록 조회", description = "개찰결과 공사 예비가격상세 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/opengResultPreparPcDetail/opengResultListInfoCnstwkPreparPcDetailJob")
	public ResponseEntity<String> opengResultListInfoCnstwkPreparPcDetailJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 3 * * ?");
		cmmnScheduleManager.deleteJob("OpengResultListInfoCnstwkPreparPcDetailJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengResultListInfoCnstwkPreparPcDetailJob.class, "OpengResultListInfoCnstwkPreparPcDetailJob", "낙찰정보", "개찰결과 공사 예비가격상세 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 개찰결과 용역 예비가격상세 목록 조회", description = "개찰결과 용역 예비가격상세 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/opengResultPreparPcDetail/opengResultListInfoServcPreparPcDetailJob")
	public ResponseEntity<String> opengResultListInfoServcPreparPcDetailJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 3 * * ?");
		cmmnScheduleManager.deleteJob("OpengResultListInfoServcPreparPcDetailJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengResultListInfoServcPreparPcDetailJob.class, "OpengResultListInfoServcPreparPcDetailJob", "낙찰정보", "개찰결과 용역 예비가격상세 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 개찰결과 외자 예비가격상세 목록 조회", description = "개찰결과 외자 예비가격상세 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/opengResultPreparPcDetail/opengResultListInfoFrgcptPreparPcDetailJob")
	public ResponseEntity<String> opengResultListInfoFrgcptPreparPcDetailJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 3 * * ?");
		cmmnScheduleManager.deleteJob("OpengResultListInfoFrgcptPreparPcDetailJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengResultListInfoFrgcptPreparPcDetailJob.class, "OpengResultListInfoFrgcptPreparPcDetailJob", "낙찰정보", "개찰결과 외자 예비가격상세 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 개찰결과 물품 예비가격상세 목록 조회", description = "개찰결과 물품 예비가격상세 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/opengResultPreparPcDetail/opengResultListInfoThngPreparPcDetailJob")
	public ResponseEntity<String> opengResultListInfoThngPreparPcDetailJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 3 * * ?");
		cmmnScheduleManager.deleteJob("OpengResultListInfoThngPreparPcDetailJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OpengResultListInfoThngPreparPcDetailJob.class, "OpengResultListInfoThngPreparPcDetailJob", "낙찰정보", "개찰결과 물품 예비가격상세 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/***************************** 낙찰정보 - 예비가격(최신데이터) *****************************/

	@Operation(summary = "예비가격 최신데이터 수집", description = "예비가격 최신데이터 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/latest/opengResultPreparPcDetail/colctLatestOpengResultPreparPcDetailInfoJob")
	public ResponseEntity<String> colctLatestOpengResultPreparPcDetailInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 */10 * * * ?");
		cmmnScheduleManager.deleteJob("ColctLatestOpengResultPreparPcDetailInfoJob", "최신자료수집"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ColctLatestOpengResultPreparPcDetailInfoJob.class, "ColctLatestOpengResultPreparPcDetailInfoJob", "최신자료수집", "예비가격 최신데이터 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}