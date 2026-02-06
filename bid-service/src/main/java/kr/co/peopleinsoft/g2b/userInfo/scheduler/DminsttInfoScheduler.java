package kr.co.peopleinsoft.g2b.userInfo.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.peopleinsoft.cmmn.quartz.manager.CmmnScheduleManager;
import kr.co.peopleinsoft.cmmn.quartz.service.CmmnSchedulerInfoService;
import kr.co.peopleinsoft.g2b.userInfo.scheduler.jobs.dminsttInfo.CollectionLastFiveYearDataJob;
import kr.co.peopleinsoft.g2b.userInfo.scheduler.jobs.dminsttInfo.CollectionTodayAndYesterdayDataJob;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Controller
@RequestMapping("/scheduler/UsrInfoService/dminsttInfoService")
@Tag(name = "사용자정보서비스(UsrInfoService)", description = "나라장터에 등록된 조달업체와 수요기관에 대한 정보를 제공하는 서비스로 조달업체정보에는 사업자등록번호, 업체명, 업체주소, 업체의 등록업종정보, 업체의 공급물품정보가 포함되며 수요기관정보에는 수요기관코드(행자부코드가 기본으로 제공되며 행자부코드가 없을 경우 나라장터 수요기관코드가 제공됨), 소관구분, 주소, 최상위기관코드, 최상위기관명 등이 포함되는 나라장터 사용자정보서비스")
public class DminsttInfoScheduler {

	private final CmmnScheduleManager cmmnScheduleManager;
	private final CmmnSchedulerInfoService cmmnSchedulerInfoService;

	public DminsttInfoScheduler(CmmnScheduleManager cmmnScheduleManager, CmmnSchedulerInfoService cmmnSchedulerInfoService) {
		this.cmmnScheduleManager = cmmnScheduleManager;
		this.cmmnSchedulerInfoService = cmmnSchedulerInfoService;
	}

	@Operation(summary = "최근 5년치 데이터 수집 (매일 저녁 9시)", description = "최근 5년치 데이터 수집")
	@GetMapping("/CollectionLastFiveYearDataJob")
	public ResponseEntity<String> CollectionLastFiveYearDataJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob("CollectionLastFiveYearDataJob", "사용자정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CollectionLastFiveYearDataJob.class, "CollectionLastFiveYearDataJob", "사용자정보", "사용자정보 - 수요기관 정보 조회", "0 0 21 * * ?", new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}

	@Operation(summary = "어제,오늘 데이터만 수집 (5분 단위 수집)", description = "어제/오늘 데이터 수집")
	@GetMapping("/CollectionTodayAndYesterdayDataJob")
	public ResponseEntity<String> CollectionTodayAndYesterdayDataJob() throws SchedulerException, JsonProcessingException {
		cmmnScheduleManager.deleteJob("CollectionTodayAndYesterdayDataJob", "사용자정보"); // 이전에 등록된 job 삭제
		cmmnScheduleManager.createCronJob(CollectionTodayAndYesterdayDataJob.class, "CollectionTodayAndYesterdayDataJob", "사용자정보", "수요기관정보 최신데이터 수집", "0 */10 * * * ?", new HashMap<>());
		String jobList = cmmnSchedulerInfoService.getAllJobsAndTriggersAsJson();
		return ResponseEntity.ok().body(jobList);
	}
}