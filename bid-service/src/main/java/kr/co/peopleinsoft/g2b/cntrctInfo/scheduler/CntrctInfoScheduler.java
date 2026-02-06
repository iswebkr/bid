package kr.co.peopleinsoft.g2b.cntrctInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.biz.controller.CmmnAbstractController;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.cntrctInfo.scheduler.jobs.CollectionLastFiveYearDataJob;
import kr.co.peopleinsoft.g2b.cntrctInfo.scheduler.jobs.CollectionTodayAndYesterdayDataJob;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

	@Operation(summary = "최근 5년치 데이터 수집 (매일 저녁 9시)", description = "최근 5년치 데이터 수집")
	@GetMapping("/CollectionLastFiveYearDataJob")
	public ResponseEntity<String> CollectionLastFiveYearDataJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob("CollectionLastFiveYearDataJob", "계약현황"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CollectionLastFiveYearDataJob.class, "CollectionLastFiveYearDataJob", "계약현황", "최근 5년치 데이터 수집", "0 0 21 * * ?", new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "어제,오늘 데이터만 수집 (5분 단위 수집)", description = "어제/오늘 데이터 수집")
	@GetMapping("/CollectionTodayAndYesterdayDataJob")
	public ResponseEntity<String> CollectionTodayAndYesterdayDataJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob("CollectionTodayAndYesterdayDataJob", "계약현황"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CollectionTodayAndYesterdayDataJob.class, "CollectionTodayAndYesterdayDataJob", "계약현황", "어제/오늘 데이터 수집", "0 */10 * * * ?", new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}