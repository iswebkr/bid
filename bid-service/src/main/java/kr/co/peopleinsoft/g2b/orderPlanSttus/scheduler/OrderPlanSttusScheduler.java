package kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler.jobs.CollectionLastFiveYearDataJob;
import kr.co.peopleinsoft.g2b.orderPlanSttus.scheduler.jobs.CollectionTodayAndYesterdayDataJob;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

	@Operation(summary = "최근 5년치 데이터 수집 (매일 저녁 9시)", description = "최근 5년치 데이터 수집")
	@GetMapping("/CollectionLastFiveYearDataJob")
	public ResponseEntity<String> CollectionLastFiveYearDataJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob("CollectionLastFiveYearDataJob", "발주계획"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CollectionLastFiveYearDataJob.class, "CollectionLastFiveYearDataJob", "발주계획", "최근 5년치 데이터 수집", "0 0 21 * * ?", new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "어제,오늘 데이터만 수집 (30분 단위 수집)", description = "어제/오늘 데이터 수집")
	@GetMapping("/CollectionTodayAndYesterdayDataJob")
	public ResponseEntity<String> CollectionTodayAndYesterdayDataJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob("CollectionTodayAndYesterdayDataJob", "발주계획"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CollectionTodayAndYesterdayDataJob.class, "CollectionTodayAndYesterdayDataJob", "발주계획", "어제/오늘 데이터 수집", "0 */30 * * * ?", new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}