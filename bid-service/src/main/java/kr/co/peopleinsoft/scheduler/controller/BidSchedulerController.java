package kr.co.peopleinsoft.scheduler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.jobs.BidPublicInfoServiceJob;
import kr.co.peopleinsoft.g2b.jobs.HrcspSsstndrdInfoJob;
import kr.co.peopleinsoft.g2b.jobs.OrderPlanSttusJob;
import kr.co.peopleinsoft.g2b.jobs.UserInfoServiceJob;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@Tag(name = "조달청 자료수집을 위한 Scheduler", description = "입찰정보 수집용 API")
public class BidSchedulerController extends CmmnAbstractController {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public BidSchedulerController(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "나라장터검색조건에 의한 입찰공고정보수집", description = "나라장터검색조건에 의한 입찰공고정보수집 (default : 매일 12시)", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/BidPublicInfoService")
	public ResponseEntity<String> bidPublicInfoService(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("BidPublicInfoService", "BidPublicInfoService"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(BidPublicInfoServiceJob.class, "BidPublicInfoService", "BidPublicInfoService", "나라장터검색조건에 의한 입찰공고정보수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "수요기관정보 수집", description = "수요기관 정보 수집 (default : 1시간 마다)", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/G2BUserInfoServiceJob/getDminsttInfo02")
	public ResponseEntity<String> getDminsttInfo02(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 */1 * * ?");
		cmmnScheduleManager.deleteJob("getDminsttInfo02", "UsrInfoService"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(UserInfoServiceJob.class, "getDminsttInfo02", "UsrInfoService", "사용자정보 - 수요기관 정보 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "발주계획정보 수집", description = "발주계획정보 수집 (2시간 마다)", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/G2BOrderPlanSttusJob")
	public ResponseEntity<String> G2BOrderPlanSttusJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 13 * * ?");
		cmmnScheduleManager.deleteJob("G2BOrderPlanSttusJob", "G2BOrderPlanSttus"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OrderPlanSttusJob.class, "G2BOrderPlanSttusJob", "G2BOrderPlanSttus", "발주계획정보수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "사전규격정보 수집", description = "사전규격정보 수집 (3시간 마다)", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/shcduler/G2BHrcspSsstndrdInfoJob")
	public ResponseEntity<String> G2BHrcspSsstndrdInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 14 * * ?");
		cmmnScheduleManager.deleteJob("G2BHrcspSsstndrdInfoJob", "G2BHrcspSsstndrdInfo"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(HrcspSsstndrdInfoJob.class, "G2BHrcspSsstndrdInfoJob", "G2BHrcspSsstndrdInfo", "사전규격정보수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "Job 목록 조회", description = "등록된 Job 목록 조회")
	@GetMapping("/shcduler/zzz/listAllJobs")
	public ResponseEntity<String> listAllJobs() throws SchedulerException, JsonProcessingException {
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "모든 Job 삭제", description = "등록된 모든 Job 삭제")
	@GetMapping("/shcduler/zzz/removeAllJob")
	public ResponseEntity<String> removeAllJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.removeAllJobs();
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "특정 Job 삭제", description = "선택적 Job 삭제 처리")
	@GetMapping("/shcduler/zzz/deleteJob")
	public ResponseEntity<String> deleteJob(String jobName, String jobGroupName) throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob(jobName, jobGroupName);
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}