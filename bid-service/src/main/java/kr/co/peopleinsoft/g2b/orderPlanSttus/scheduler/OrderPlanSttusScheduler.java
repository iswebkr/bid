package kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler.jobs.ColctLatestOrderPlanSttusJob;
import kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler.jobs.OrderPlanSttusListCnstwkPPSSrchJob;
import kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler.jobs.OrderPlanSttusListFrgcptPPSSrchJob;
import kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler.jobs.OrderPlanSttusListServcPPSSrchJob;
import kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler.jobs.OrderPlanSttusListThngPPSSrchJob;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Controller
@RequestMapping("/scheduler/OrderPlanSttusService")
@Tag(name = "나라장터 발주계획현황서비스(OrderPlanSttusService)", description = "발주기관들이 나라장터에 등록한 발주계획정보를 제공하는 서비스로 각 발주기관들이 당해 회계연도에 조달할 공사, 물품, 용역, 외자에 대한 분기별 발주계획(조달대상, 예산액, 발주예정시기, 발주방법, 발주기관 주소, 연락처 등) 공고 내역를 제공하는 나라장터 발주계획현황서비스")
public class OrderPlanSttusScheduler {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public OrderPlanSttusScheduler(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회", description = "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/orderPlanSttusListCnstwkPPSSrchJob")
	public ResponseEntity<String> orderPlanSttusListCnstwkPPSSrchJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 11 * * ?");
		cmmnScheduleManager.deleteJob("OrderPlanSttusListCnstwkPPSSrchJob", "발주계획"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OrderPlanSttusListCnstwkPPSSrchJob.class, "OrderPlanSttusListCnstwkPPSSrchJob", "발주계획", "나라장터 검색조건에 의한 발주계획현황에 대한 공사조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회", description = "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/orderPlanSttusListServcPPSSrch")
	public ResponseEntity<String> orderPlanSttusListServcPPSSrch(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 11 * * ?");
		cmmnScheduleManager.deleteJob("OrderPlanSttusListServcPPSSrchJob", "발주계획"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OrderPlanSttusListServcPPSSrchJob.class, "OrderPlanSttusListServcPPSSrchJob", "발주계획", "나라장터 검색조건에 의한 발주계획현황에 대한 용역조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회", description = "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/orderPlanSttusListFrgcptPPSSrchJob")
	public ResponseEntity<String> orderPlanSttusListFrgcptPPSSrchJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 11 * * ?");
		cmmnScheduleManager.deleteJob("OrderPlanSttusListFrgcptPPSSrchJob", "발주계획"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OrderPlanSttusListFrgcptPPSSrchJob.class, "OrderPlanSttusListFrgcptPPSSrchJob", "발주계획", "나라장터 검색조건에 의한 발주계획현황에 대한 외자조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회", description = "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/orderPlanSttusListThngPPSSrchJob")
	public ResponseEntity<String> orderPlanSttusListThngPPSSrchJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 0 11 * * ?");
		cmmnScheduleManager.deleteJob("OrderPlanSttusListThngPPSSrchJob", "발주계획"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(OrderPlanSttusListThngPPSSrchJob.class, "OrderPlanSttusListThngPPSSrchJob", "발주계획", "나라장터 검색조건에 의한 발주계획현황에 대한 물품조회", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	/***************************** 발주계획(최신데이터) *****************************/

	@Operation(summary = "최신 발주계획 자료 수집", description = "최신 발주계획 자료 수집", parameters = {
		@Parameter(name = "jobExpression", description = "Quartz 크론표현식 (ex : * 0 * * * ?) [초, 분, 시, 일, 월, 주, 년]", allowEmptyValue = true)
	})
	@GetMapping("/colctLatestOrderPlanSttusJob")
	public ResponseEntity<String> colctLatestOrderPlanSttusJob(@RequestParam(required = false) String jobExpression) throws SchedulerException, JsonProcessingException {
		String cronJobExpression = StringUtils.defaultIfBlank(jobExpression, "0 */10 * * * ?");
		cmmnScheduleManager.deleteJob("ColctLatestOrderPlanSttusJob", "최신자료수집"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(ColctLatestOrderPlanSttusJob.class, "ColctLatestOrderPlanSttusJob", "최신자료수집", "최신 발주계획 자료 수집", cronJobExpression, new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}