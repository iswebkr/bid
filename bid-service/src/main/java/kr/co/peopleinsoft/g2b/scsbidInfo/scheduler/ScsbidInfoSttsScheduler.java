package kr.co.peopleinsoft.g2b.scsbidInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.scsbidInfoStts.ColctLatestScsbidInfoSttsJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.scsbidInfoStts.ScsbidListSttusCnstwkPPSSrchJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.scsbidInfoStts.ScsbidListSttusFrgcptPPSSrchJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.scsbidInfoStts.ScsbidListSttusServcPPSSrchJob;
import kr.co.peopleinsoft.g2b.scsbidInfo.scheduler.jobs.scsbidInfoStts.ScsbidListSttusThngPPSSrchJob;
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
public class ScsbidInfoSttsScheduler {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public ScsbidInfoSttsScheduler(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "낙찰정보 - 나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회", description = "나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/scsbidInfoStts/scsbidListSttusCnstwkPPSSrchJob")
	public ResponseEntity<String> scsbidListSttusCnstwkPPSSrchJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 4 * * ?");
		cmmnScheduleManager.deleteJob("ScsbidListSttusCnstwkPPSSrchJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ScsbidListSttusCnstwkPPSSrchJob.class, "ScsbidListSttusCnstwkPPSSrchJob", "낙찰정보", "나라장터 검색조건에 의한 낙찰된 목록 현황 공사조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회", description = "나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/scsbidInfoStts/scsbidListSttusServcPPSSrchJob")
	public ResponseEntity<String> scsbidListSttusServcPPSSrchJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 4 * * ?");
		cmmnScheduleManager.deleteJob("ScsbidListSttusServcPPSSrchJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ScsbidListSttusServcPPSSrchJob.class, "ScsbidListSttusServcPPSSrchJob", "낙찰정보", "나라장터 검색조건에 의한 낙찰된 목록 현황 용역조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회", description = "나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/scsbidInfoStts/scsbidListSttusFrgcptPPSSrchJob")
	public ResponseEntity<String> scsbidListSttusFrgcptPPSSrchJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 4 * * ?");
		cmmnScheduleManager.deleteJob("ScsbidListSttusFrgcptPPSSrchJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ScsbidListSttusFrgcptPPSSrchJob.class, "ScsbidListSttusFrgcptPPSSrchJob", "낙찰정보", "나라장터 검색조건에 의한 낙찰된 목록 현황 외자조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "낙찰정보 - 나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회", description = "나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/scsbidInfoStts/scsbidListSttusThngPPSSrchJob")
	public ResponseEntity<String> scsbidListSttusThngPPSSrchJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 4 * * ?");
		cmmnScheduleManager.deleteJob("ScsbidListSttusThngPPSSrchJob", "낙찰정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ScsbidListSttusThngPPSSrchJob.class, "ScsbidListSttusThngPPSSrchJob", "낙찰정보", "나라장터 검색조건에 의한 낙찰된 목록 현황 물품조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/***************************** 낙찰정보 - 낙찰목록(최신데이터) *****************************/

	@Operation(summary = "낙찰목록 최신데이터 수집", description = "낙찰목록 최신데이터 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/scsbidInfoStts/colctLatestScsbidInfoSttsJob")
	public ResponseEntity<String> colctLatestScsbidInfoSttsJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 */10 * * * ?");
		cmmnScheduleManager.deleteJob("ColctLatestScsbidInfoSttsJob", "최신자료수집"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ColctLatestScsbidInfoSttsJob.class, "ColctLatestScsbidInfoSttsJob", "최신자료수집", "낙찰목록 최신데이터 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}