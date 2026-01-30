package kr.co.peopleinsoft.scheduler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/scheduler")
@Tag(name = "@Scheduler Management", description = "Scheduler 를 등록 관리하고, 특정 Scheduler 의 실행을 멈추거나 전체 Scheduler 를 실행하는 등 등록된 모든 Scheduler 에 대한 관리를 위한 API")
public class BidSchedulerController extends CmmnAbstractController {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public BidSchedulerController(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "Job 목록 조회", description = "등록된 Job 목록 조회")
	@GetMapping("/jobs/listAllJobs")
	public ResponseEntity<String> listAllJobs() throws SchedulerException, JsonProcessingException {
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "모든 Job 삭제", description = "등록된 모든 Job 삭제")
	@GetMapping("/jobs/removeAllJob")
	public ResponseEntity<String> removeAllJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.removeAllJobs();
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "특정 Job 삭제", description = "선택적 Job 삭제 처리")
	@GetMapping("/jobs/deleteJob")
	public ResponseEntity<String> deleteJob(String jobName, String jobGroupName) throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob(jobName, jobGroupName);
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}