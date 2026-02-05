package kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.scheduler.jobs.CollectionLastFiveYearDataJob;
import kr.co.peopleinsoft.g2b.hrcspSsstndrdInfo.scheduler.jobs.CollectionTodayAndYesterdayDataJob;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

	@Operation(summary = "최근 5년치 데이터 수집 (매일 저녁 9시)", description = "최근 5년치 데이터 수집")
	@GetMapping("/CollectionLastFiveYearDataJob")
	public ResponseEntity<String> CollectionLastFiveYearDataJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob("CollectionLastFiveYearDataJob", "최신자료수집"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CollectionLastFiveYearDataJob.class, "CollectionLastFiveYearDataJob", "최신자료수집", "최근 5년치 데이터 수집", "0 0 21 * * ?", new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "어제,오늘 데이터만 수집 (30분 단위 수집)", description = "어제/오늘 데이터 수집")
	@GetMapping("/CollectionTodayAndYesterdayDataJob")
	public ResponseEntity<String> CollectionTodayAndYesterdayDataJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob("CollectionTodayAndYesterdayDataJob", "사전규격"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CollectionTodayAndYesterdayDataJob.class, "CollectionTodayAndYesterdayDataJob", "사전규격", "어제/오늘 데이터 수집", "0 */30 * * * ?", new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}