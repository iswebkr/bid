package kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.scheduler.jobs.ColctLatestPublicPrcureThngInfoJob;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.scheduler.jobs.PublicPrcureThngInfoCnstwkJob;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.scheduler.jobs.PublicPrcureThngInfoFrgcptJob;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.scheduler.jobs.PublicPrcureThngInfoServcJob;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.scheduler.jobs.PublicPrcureThngInfoThngJob;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@RequestMapping("/scheduler/HrcspSsstndrdInfoService")
@Tag(name = "나라장터 사전규격정보서비스(HrcspSsstndrdInfoService)", description = "물품, 용역, 외자, 공사 업무별로 나라장터에 공개된 사전규격정보를 제공하는 서비스로 업무별 사전규격 전체목록 및 기관별, 품목별로 사전규격을 조회할 수 있으며 사전규격등록번호, 품명(사업명), 배정예산액, 관련규격서파일, 규격서 의견 등을 제공하는 나라장터 사전규격정보서비스")
public class HrcspSsstndrdInfoScheduler {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public HrcspSsstndrdInfoScheduler(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "사전규격 공사 목록 조회", description = "사전규격 공사 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/PublicPrcureThngInfoCnstwkJob")
	public ResponseEntity<String> PublicPrcureThngInfoCnstwkJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("PublicPrcureThngInfoCnstwkJob", "사전규격"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(PublicPrcureThngInfoCnstwkJob.class, "PublicPrcureThngInfoCnstwkJob", "사전규격", "사전규격 공사 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "사전규격 용역 목록 조회", description = "사전규격 용역 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/PublicPrcureThngInfoServcJob")
	public ResponseEntity<String> PublicPrcureThngInfoServcJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("PublicPrcureThngInfoServcJob", "사전규격"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(PublicPrcureThngInfoServcJob.class, "PublicPrcureThngInfoServcJob", "사전규격", "사전규격 용역 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "사전규격 외자 목록 조회", description = "사전규격 외자 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/PublicPrcureThngInfoFrgcptJob")
	public ResponseEntity<String> PublicPrcureThngInfoFrgcptJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("PublicPrcureThngInfoFrgcptJob", "사전규격"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(PublicPrcureThngInfoFrgcptJob.class, "PublicPrcureThngInfoFrgcptJob", "사전규격", "사전규격 외자 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "사전규격 물품 목록 조회", description = "사전규격 물품 목록 조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/PublicPrcureThngInfoThngJob")
	public ResponseEntity<String> PublicPrcureThngInfoThngJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 12 * * ?");
		cmmnScheduleManager.deleteJob("PublicPrcureThngInfoThngJob", "사전규격"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(PublicPrcureThngInfoThngJob.class, "PublicPrcureThngInfoThngJob", "사전규격", "사전규격 물품 목록 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/***************************** 사전규격(최신데이터) *****************************/

	@Operation(summary = "사전규격 최신데이터 수집", description = "사전규격 최신데이터 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/latest/colctLatestPublicPrcureThngInfoJob")
	public ResponseEntity<String> colctLatestPublicPrcureThngInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 */10 * * * ?");
		cmmnScheduleManager.deleteJob("ColctLatestPublicPrcureThngInfoJob", "최신자료수집"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ColctLatestPublicPrcureThngInfoJob.class, "ColctLatestPublicPrcureThngInfoJob", "최신자료수집", "사전규격 최신데이터 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}