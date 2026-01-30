package kr.co.peopleinsoft.g2b.userInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.userInfo.scheduler.jobs.dminsttInfo.ColctLatestDminsttInfoJob;
import kr.co.peopleinsoft.g2b.userInfo.scheduler.jobs.dminsttInfo.DminsttInfoJob;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@RequestMapping("/scheduler/UsrInfoService")
@Tag(name = "사용자정보서비스(UsrInfoService)", description = "나라장터에 등록된 조달업체와 수요기관에 대한 정보를 제공하는 서비스로 조달업체정보에는 사업자등록번호, 업체명, 업체주소, 업체의 등록업종정보, 업체의 공급물품정보가 포함되며 수요기관정보에는 수요기관코드(행자부코드가 기본으로 제공되며 행자부코드가 없을 경우 나라장터 수요기관코드가 제공됨), 소관구분, 주소, 최상위기관코드, 최상위기관명 등이 포함되는 나라장터 사용자정보서비스")
public class DminsttInfoScheduler {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public DminsttInfoScheduler(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "수요기관정보 수집", description = "수요기관 정보 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/dminsttInfoJob")
	public ResponseEntity<String> dminsttInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 */1 * * ?");
		cmmnScheduleManager.deleteJob("DminsttInfoJob", "사용자정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(DminsttInfoJob.class, "DminsttInfoJob", "사용자정보", "사용자정보 - 수요기관 정보 조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/***************************** 사용자정보 - 수요기관정보(최신데이터) *****************************/

	@Operation(summary = "수요기관정보 최신데이터 수집", description = "수요기관정보 최신데이터 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/colctLatestDminsttInfoJob")
	public ResponseEntity<String> colctLatestDminsttInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 */1 * * ?");
		cmmnScheduleManager.deleteJob("ColctLatestDminsttInfoJob", "최신자료수집"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ColctLatestDminsttInfoJob.class, "ColctLatestDminsttInfoJob", "최신자료수집", "수요기관정보 최신데이터 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}