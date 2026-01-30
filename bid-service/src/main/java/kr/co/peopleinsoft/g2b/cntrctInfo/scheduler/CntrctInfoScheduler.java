package kr.co.peopleinsoft.g2b.cntrctInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.cntrctInfo.scheduler.jobs.CntrctInfoListCnstwkPPSSrchJob;
import kr.co.peopleinsoft.g2b.cntrctInfo.scheduler.jobs.CntrctInfoListFrgcptPPSSrchJob;
import kr.co.peopleinsoft.g2b.cntrctInfo.scheduler.jobs.CntrctInfoListServcPPSSrchJob;
import kr.co.peopleinsoft.g2b.cntrctInfo.scheduler.jobs.CntrctInfoListThngPPSSrchJob;
import kr.co.peopleinsoft.g2b.cntrctInfo.scheduler.jobs.ColctLatestCntrctInfoJob;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@RequestMapping("/scheduler/CntrctInfoService")
@Tag(
	name = "나라장터 계약정보서비스(CntrctInfoService)"
	, description = "나라장터에서 체결된 계약정보목록을 물품, 외자, 공사, 용역의각 업무별로 제공하는 서비스로, 각 업무별 계약상세정보, 계약변경이력정보, 계약삭제이력정보를 제공. 또한, 나라장터 검색조건인 계약체결일자, 확정계약번호, 요청번호, 공고번호, 기관명(계약기관, 수요기관), 품명, 계약방법, 계약참조번호에 따른 계약현황정보를 제공\n" +
	". 변경된 계약정보이력조회\n" +
	". 삭제된 계약정보조회\n" +
	". 나라장터 검색조건에 의한 계약정보 조회")
public class CntrctInfoScheduler extends CmmnAbstractController {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public CntrctInfoScheduler(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 공사조회", description = "나라장터검색조건에 의한 계약현황 공사조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/cntrctInfoListCnstwkPPSSrchJob")
	public ResponseEntity<String> cntrctInfoListCnstwkPPSSrchJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 1 * * ?");
		cmmnScheduleManager.deleteJob("CntrctInfoListCnstwkPPSSrchJob", "계약현황"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CntrctInfoListCnstwkPPSSrchJob.class, "CntrctInfoListCnstwkPPSSrchJob", "계약현황", "나라장터검색조건에 의한 계약현황 공사조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 용역조회", description = "나라장터검색조건에 의한 계약현황 용역조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/cntrctInfoListServcPPSSrchJob")
	public ResponseEntity<String> cntrctInfoListServcPPSSrchJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 1 * * ?");
		cmmnScheduleManager.deleteJob("CntrctInfoListServcPPSSrchJob", "계약현황"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CntrctInfoListServcPPSSrchJob.class, "CntrctInfoListServcPPSSrchJob", "계약현황", "나라장터검색조건에 의한 계약현황 용역조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 외자조회", description = "나라장터검색조건에 의한 계약현황 외자조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/cntrctInfoListFrgcptPPSSrchJob")
	public ResponseEntity<String> cntrctInfoListFrgcptPPSSrchJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 1 * * ?");
		cmmnScheduleManager.deleteJob("CntrctInfoListFrgcptPPSSrchJob", "계약현황"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CntrctInfoListFrgcptPPSSrchJob.class, "CntrctInfoListFrgcptPPSSrchJob", "계약현황", "나라장터검색조건에 의한 계약현황 외자조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터검색조건에 의한 계약현황 물품조회", description = "나라장터검색조건에 의한 계약현황 물품조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/cntrctInfoListThngPPSSrchJob")
	public ResponseEntity<String> cntrctInfoListThngPPSSrchJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 1 * * ?");
		cmmnScheduleManager.deleteJob("CntrctInfoListThngPPSSrchJob", "계약현황"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CntrctInfoListThngPPSSrchJob.class, "CntrctInfoListThngPPSSrchJob", "계약현황", "나라장터검색조건에 의한 계약현황 물품조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/***************************** 계약현황(최신데이터) *****************************/

	@Operation(summary = "계약현황 최신데이터 수집", description = "계약현황 최신데이터 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/latest/colctLatestCntrctInfoJob")
	public ResponseEntity<String> colctLatestCntrctInfoJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 */20 * * * ?");
		cmmnScheduleManager.deleteJob("ColctLatestCntrctInfoJob", "최신자료수집"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ColctLatestCntrctInfoJob.class, "ColctLatestCntrctInfoJob", "최신자료수집", "계약현황 최신데이터 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}